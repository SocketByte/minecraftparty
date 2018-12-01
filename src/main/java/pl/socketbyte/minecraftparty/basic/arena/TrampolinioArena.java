package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.trampolinio.Baloon;
import pl.socketbyte.minecraftparty.basic.arena.helper.trampolinio.BaloonData;
import pl.socketbyte.minecraftparty.basic.arena.helper.trampolinio.BaloonType;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TrampolinioArena extends Arena {

    private int radius;
    private int target;

    private boolean jumping = false;

    private List<Baloon> baloons = new CopyOnWriteArrayList<>();
    private Map<BaloonType, Double> chances = new HashMap<>();
    private Map<BaloonType, BaloonData> dataMap = new HashMap<>();

    public TrampolinioArena(Game game) {
        super(game);
    }

    public void readBaloonData() {
        ConfigurationSection info = getArenaInfo().getData().getConfigurationSection("info");

        for (String key : info.getKeys(false)) {
            ConfigurationSection section = info.getConfigurationSection(key);

            BaloonData baloon = new BaloonData(BaloonType.valueOf(key.toUpperCase()));
            baloon.setChance(section.getDouble("chance"));
            baloon.setMinY(section.getInt("height.min"));
            baloon.setMaxY(section.getInt("height.max"));
            baloon.setTitle(MessageHelper.fixColor(section.getString("title")));

            chances.put(baloon.getType(), baloon.getChance());
            dataMap.put(baloon.getType(), baloon);
        }
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(),
                getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {

    }

    @Override
    public void onTick(long timeLeft) {
        if (baloons.size() != target) {
            spawn(target - baloons.size());
        }
    }

    private Map<BaloonType, Integer> amounts = new ConcurrentHashMap<>();
    private void countAll() {
        amounts.clear();
        for (BaloonType type : BaloonType.values()) {
            amounts.put(type, 0);
        }
        for (Baloon baloon : baloons) {
            amounts.put(baloon.getData().getType(), amounts.get(baloon.getData().getType()) + 1);
        }
    }

    public void spawn(int amount) {
        countAll();
        for (int i = 0; i < amount; i++) {
            BaloonType pick = RandomHelper.pick(chances);
            if (pick == null) {
                continue;
            }
            BaloonData data = dataMap.get(pick);

            Location location = randomizeLocation(data);
            boolean cancel = false;
            for (Baloon baloon : baloons) {
                if (baloon.getLocation().equals(location)) {
                    cancel = true;
                    break;
                }
            }
            if (cancel)
                continue;

            int limit = data.getType().getDivideLimit();
            int typeAmount = amounts.get(data.getType());
            int realLimit;
            int boosterAmount = amounts.get(BaloonType.BOOSTER);
            if (boosterAmount < 1) {
                data = dataMap.get(BaloonType.BOOSTER);
            }
            else if (limit != 0) {
                realLimit = baloons.size() / limit;

                if (typeAmount >= realLimit)
                    continue;
            }

            Baloon baloon = new Baloon(data);
            baloon.setLocation(location);
            baloon.place();
            baloons.add(baloon);
            countAll();
        }
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
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        Player player = event.getPlayer();

        TaskHelper.async(() -> {
            if (jumping) {
                if (!player.getLocation().clone().subtract(0, 1, 0)
                        .getBlock().getType().equals(Material.AIR)) {
                    player.setVelocity(player.getLocation().getDirection().multiply(new Vector(0.5, 1, 0.5)).setY(0.8));
                }
            }

            for (Baloon baloon : baloons) {
                if (baloon.isColliding(player.getLocation())) {
                    baloon.kill();

                    addInternalScore(player, baloon.getData().getPoints());
                    MessageHelper.send(player, baloon.getData().getTitle());

                    baloons.remove(baloon);

                    if (baloon.getData().getType() == BaloonType.BOOSTER) {
                        player.setVelocity(player.getLocation().getDirection().setY(1.4));
                    }
                }
            }
        });
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

    public Location randomizeLocation(BaloonData data) {
        Location location = getArenaInfo().getDefaultLocation().clone();

        int randomX = RandomHelper.randomInteger(-radius, radius);
        int randomY = RandomHelper.randomInteger(data.getMinY(), data.getMaxY());
        int randomZ = RandomHelper.randomInteger(-radius, radius);

        location.add(randomX, randomY, randomZ);

        return location;
    }

    @Override
    public void onFreeze() {
        this.jumping = false;
        for (Baloon baloon : baloons) {
            baloon.kill();
        }
    }

    @Override
    public void onInit() {
        this.getBoard().setType(ArenaBoardType.SCORES);

        this.radius = getArenaInfo().getData().getInt("radius");
        this.target = getArenaInfo().getData().getInt("target");
        readBaloonData();
    }

    @EventHandler
    public void onSandFall(EntityChangeBlockEvent event){
        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
        event.getBlock().getState().update(false, false);
    }

    @Override
    public void onStart() {
        this.jumping = true;
    }

    @Override
    public void onEnd() {

    }

}