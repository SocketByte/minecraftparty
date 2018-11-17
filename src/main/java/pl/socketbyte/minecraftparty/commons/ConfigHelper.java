package pl.socketbyte.minecraftparty.commons;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {

    private ConfigHelper() {
    }

    public static Location readLocation(ConfigurationSection section) {
        return new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"));
    }

    public static Location[] readMinMaxLocation(World world, ConfigurationSection section) {
        Location[] array = new Location[2];

        array[0] = new Location(world,
                section.getDouble("min.x"),
                section.getDouble("min.y"),
                section.getDouble("min.z"));
        array[1] = new Location(world,
                section.getDouble("max.x"),
                section.getDouble("max.y"),
                section.getDouble("max.z"));

        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> map(ConfigurationSection section, Class<T> valueType) {
        Map<String, T> map = new HashMap<>();
        for (String key : section.getKeys(false)) {
            map.put(key, (T) section.get(key));
        }
        return map;
    }
}
