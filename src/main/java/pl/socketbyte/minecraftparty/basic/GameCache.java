package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameCache {

    private final Map<UUID, Location> positionCache = new ConcurrentHashMap<>();
    private final Map<UUID, PlayerInventory> inventoryCache = new ConcurrentHashMap<>();

    public void addPositionCache(Player player) {
        this.positionCache.put(player.getUniqueId(), player.getLocation());
    }

    public void addInventoryCache(Player player) {
        this.inventoryCache.put(player.getUniqueId(), player.getInventory());
    }

    public Location getPositionCache(Player player) {
        return this.positionCache.get(player.getUniqueId());
    }

    public PlayerInventory getInventoryCache(Player player) {
        return this.inventoryCache.get(player.getUniqueId());
    }

    public void giveItemsBack(Player player) {
        PlayerInventory inventory = getInventoryCache(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(inventory.getArmorContents());
        player.getInventory().setContents(inventory.getContents());
        player.getInventory().setItemInHand(inventory.getItemInHand());
        player.getInventory().setHeldItemSlot(inventory.getHeldItemSlot());
    }

    public void teleportBack(Player player) {
        player.teleport(getPositionCache(player));
        this.positionCache.remove(player.getUniqueId());
    }
}
