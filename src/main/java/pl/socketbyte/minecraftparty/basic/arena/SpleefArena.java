package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.List;

public class SpleefArena extends Arena {
    private Location min;
    private Location max;

    public SpleefArena(Game game) {
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

    }

    @Override
    public void onFreeze() {
        generate();
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            player.teleport(getArenaInfo().getDefaultLocation());
        }
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.DISQUALIFICATION);

        Location[] minMax = ConfigHelper.readMinMaxLocation(getArenaInfo().getDefaultLocation().getWorld(),
                getArenaInfo().getData().getConfigurationSection("locations"));
        min = minMax[0];
        max = minMax[1];
        generate();
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        if (isDisqualified(event.getPlayer()))
            return;

        if (isCountdown() || isFreezed())
            return;

        Player player = event.getPlayer();
        if (player.getLocation().getY() < min.getY() + 1.0) {
            disqualify(player);
            player.getInventory().clear();
            player.teleport(getArenaInfo().getDefaultLocation().clone().add(0, 15, 0));

            MessageHelper.send(player, I18n.get().message("spleef.died"));
        }
    }

    public void generate() {
        System.out.println("generating");
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                        x, min.getBlockY(), z);

                location.getBlock().setType(Material.SNOW_BLOCK);
            }
        }
    }

    @Override
    public void onStart() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SPADE));
            player.getInventory().setHeldItemSlot(0);
        }
    }

    @Override
    public void onEnd() {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        Player player = event.getPlayer();

        event.setCancelled(true);
        if (isCountdown())
            return;

        if (event.getBlock().getType() == Material.SNOW_BLOCK) {
            event.getBlock().setType(Material.AIR);
        }
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

}
