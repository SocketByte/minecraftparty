package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameCache {

    private final Map<UUID, UserInfo> cache = new ConcurrentHashMap<>();

    public void addCache(Player player) {
        this.cache.put(player.getUniqueId(), new UserInfo(player));
    }

    public UserInfo getCache(Player player) {
        return this.cache.get(player.getUniqueId());
    }

    public void apply(Player player) {
        UserInfo info = getCache(player);

        info.apply(player);

        this.cache.remove(player.getUniqueId());
    }
}
