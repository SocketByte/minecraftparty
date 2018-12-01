package pl.socketbyte.minecraftparty.basic.arena;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.woolmixup.WoolCell;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WoolMixupArena extends Arena {

    private final List<WoolCell> cells = new ArrayList<>();

    private EditSession session;
    private Location min;
    private Location max;

    private int baseTime;
    private int timePassed;
    private boolean waveInProgress;

    private int disqualifySpawnY;
    private int disqualifyMinY;
    private int disqualifyMaxY;

    private final Map<DyeColor, String> colors = new HashMap<>();

    private DyeColor currentColor;
    private int countdownTime;

    public WoolMixupArena(Game game) {
        super(game);
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(), getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {

    }

    @Override
    public void onTick(long timeLeft) {
        if (waveInProgress) {
            for (Player player : getGame().getPlaying()) {
                player.setLevel(countdownTime);
            }
            countdownTime--;
        }

        nextWave();
    }

    private void nextWave() {
        if (waveInProgress)
            return;
        waveInProgress = true;

        randomizeFloor();
        Set<DyeColor> colors = this.colors.keySet();
        int random = RandomHelper.randomInteger(0, colors.size());
        int index = -1;
        for (DyeColor color : colors) {
            index++;
            if (index != random)
                continue;

            currentColor = color;
            break;
        }

        getGame().broadcastActionBar(this.colors.get(currentColor));

        for (Player player : getGame().getPlaying()) {
            player.closeInventory();
            player.getInventory().clear();

            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, new ItemStack(Material.WOOL, 1, currentColor.getData()));
            }
        }

        countdownTime = baseTime;

        TaskHelper.delay(() -> {
            for (Player player : getGame().getPlaying()) {
                player.closeInventory();
                player.getInventory().clear();
                player.setLevel(0);
            }

            excludeExcept(currentColor);

            TaskHelper.delay(() -> {
                if (baseTime > 1) {
                    baseTime--;
                }
                waveInProgress = false;
            }, 5, TimeUnit.SECONDS);
        }, baseTime + 1, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(final EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!getGame().isPlaying((Player) e.getEntity()))
            return;

        if (!getGame().isArena(this))
            return;
        Player p = (Player) e.getEntity();
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }


    @Override
    public void onFreeze() {
        makeWhite();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!getGame().isPlaying((Player) event.getWhoClicked()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrop(PlayerDropItemEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!getGame().isPlaying((Player) event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        TaskHelper.async(() -> {
            Player player = event.getPlayer();

            Location location = player.getLocation();

            if (isDisqualified(player)) {
                if (location.getBlockY() < disqualifyMinY) {
                    event.setCancelled(true);
                    event.setTo(event.getFrom());
                    return;
                }

                if (location.getBlockY() > disqualifyMaxY) {
                    event.setCancelled(true);
                    event.setTo(event.getFrom());
                    return;
                }

                if (location.getBlockX() < this.min.getBlockX() || location.getBlockX() > this.max.getBlockX()
                        || location.getBlockZ() < this.min.getBlockZ() || location.getBlockZ() > this.max.getBlockZ()) {
                    event.setCancelled(true);
                    event.setTo(event.getFrom());
                    return;
                }
            }

            if (location.getBlockY() <= this.min.getY()) {
                disqualify(player);
                player.teleport(getArenaInfo().getDefaultLocation().clone().add(0, disqualifySpawnY, 0));
                MessageHelper.send(player, I18n.get().message("wool-mix-up.died"));
            }
        });
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.DISQUALIFICATION);

        session = new EditSessionBuilder(FaweAPI.getWorld(getArenaInfo().getDefaultLocation().getWorld().getName()))
                .fastmode(true).build();

        baseTime = getArenaInfo().getData().getInt("baseTime");
        disqualifyMinY = getArenaInfo().getData().getInt("disqualify-data.min-y") + getArenaInfo().getDefaultLocation().getBlockY();
        disqualifyMaxY = getArenaInfo().getData().getInt("disqualify-data.max-y") + getArenaInfo().getDefaultLocation().getBlockY();
        disqualifySpawnY = getArenaInfo().getData().getInt("disqualify-data.spawn-y") ;

        ConfigurationSection section = getArenaInfo().getData().getConfigurationSection("colors");
        for (String key : section.getKeys(false)) {
            DyeColor color = DyeColor.valueOf(key);

            colors.put(color, section.getString(key));
        }

        Location[] locations = ConfigHelper.readMinMaxLocation(
                getArenaInfo().getDefaultLocation().getWorld(),
                getArenaInfo().getData().getConfigurationSection("locations"));
        this.min = locations[0];
        this.max = locations[1];

        int step = 4;
        int cellsX = (this.max.getBlockX() - this.min.getBlockX()) / step;
        int cellsZ = (this.max.getBlockZ() - this.min.getBlockZ()) / step;

        int y = this.min.getBlockY();
        int offsetX;
        int offsetZ;
        for (int cellX = 0; cellX < cellsX; cellX++) {
            for (int cellZ = 0; cellZ < cellsZ; cellZ++) {
                offsetX = (cellX * step) + step;
                offsetZ = (cellZ * step) + step;

                determineWoolCell(step, y, offsetX, offsetZ);
            }
        }
        makeWhite();
    }

    public void determineWoolCell(int step, int y, int offsetX, int offsetZ) {
        WoolCell cell = new WoolCell();
        for (int x = this.min.getBlockX() + (offsetX - step); x < this.min.getBlockX() + offsetX; x++) {
            for (int z = this.min.getBlockZ() + (offsetZ - step); z < this.min.getBlockZ() + offsetZ; z++) {
                Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                        x, y, z);
                cell.setLocation(location);
            }
        }
        cells.add(cell);
    }

    public void excludeExcept(DyeColor color) {
        for (WoolCell cell : cells) {
            DyeColor[] colors = DyeColor.values();

            if (cell.getColor() != color) {
                cell.remove(session);
            }
        }
        session.flushQueue();
    }

    public void makeWhite() {
        for (WoolCell cell : cells) {
            DyeColor[] colors = DyeColor.values();

            cell.paint(session, DyeColor.WHITE);
        }
        session.flushQueue();
    }

    public void randomizeFloor() {
        for (WoolCell cell : cells) {
            Set<DyeColor> colors = this.colors.keySet();
            int random = RandomHelper.randomInteger(0, colors.size());
            int index = -1;
            for (DyeColor color : colors) {
                index++;
                if (index != random)
                    continue;

                cell.paint(session, color);
                break;
            }
        }
        session.flushQueue();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }
}
