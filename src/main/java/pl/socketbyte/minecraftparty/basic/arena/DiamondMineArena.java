package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;

import java.util.List;

public class DiamondMineArena extends Arena {

    private Location min;
    private Location max;
    private int probability;

    public DiamondMineArena(Game game) {
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
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            player.teleport(getArenaInfo().getDefaultLocation());
        }
        generate(0);
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);

        Location[] minMax = ConfigHelper.readMinMaxLocation(getArenaInfo().getDefaultLocation().getWorld(),
                getArenaInfo().getData().getConfigurationSection("locations"));
        min = minMax[0];
        max = minMax[1];

        probability = getArenaInfo().getData().getInt("probability");

        generate(0);
    }

    public void generate(int probability) {
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y < max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                    Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                            x, y, z);

                    if (RandomHelper.chance(probability))
                        location.getBlock().setType(Material.DIAMOND_ORE);
                    else location.getBlock().setType(Material.STONE);
                }
            }
        }
    }

    @Override
    public void onStart() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_PICKAXE));
            player.getInventory().setHeldItemSlot(0);
        }
        generate(probability);
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

        if (event.getBlock().getType().equals(Material.DIAMOND_ORE)) {
            addInternalScore(player, 1);
            player.getInventory().addItem(new ItemStack(Material.DIAMOND));
            event.getBlock().setType(Material.AIR);
        }
        else if (event.getBlock().getType().equals(Material.STONE)) {
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

    @Override
    public void onEnd() {

    }
}
