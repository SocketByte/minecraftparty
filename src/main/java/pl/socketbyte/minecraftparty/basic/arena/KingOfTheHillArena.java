package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.RandomHelper;

import java.util.List;

public class KingOfTheHillArena extends Arena {
    public KingOfTheHillArena(Game game) {
        super(game);
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

    }

    @Override
    public void onFreeze() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
        }
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);
    }

    @Override
    public void onStart() {
        setPvp(true);
        setInfiniteHealth(true);
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
            ItemStack item = new ItemStack(Material.STICK);
            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
            player.getInventory().setItem(0, item);
            player.getInventory().setHeldItemSlot(0);
        }

        getTaskManager().schedule(() -> {
            if (isFreezed() || !isActive())
                return;
            for (Player player : getGame().getPlaying()) {
                Block relative = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                if (relative.getType() == Material.STAINED_CLAY && relative.getData() == 14) {
                    if (getInternalScore(player) <= 0)
                        continue;
                    setInternalScore(player, getInternalScore(player) - 1);
                }
                else if (relative.getType() == Material.STAINED_CLAY && relative.getData() == 5) {
                    addInternalScore(player, 1);
                }
            }
        }, 5);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        Player player = event.getPlayer();

        event.setCancelled(true);
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
        setPvp(false);
        setInfiniteHealth(false);
    }
}
