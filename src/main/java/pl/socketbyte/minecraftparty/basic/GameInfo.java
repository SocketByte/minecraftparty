package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;

import java.util.*;

public class GameInfo {

    private static final Map<String, GameInfo> infoMap = new HashMap<>();

    public static GameInfo get(String id) {
        return infoMap.get(id);
    }

    private final ConfigurationSection section;
    private final Location lobbyLocation;
    private final List<ArenaInfo> arenaInfos;

    public GameInfo(ConfigurationSection section) {
        this.section = section;
        this.arenaInfos = loadArenaInfos();
        this.lobbyLocation = loadLobbyLocation();
        add();
    }

    private void add() {
        infoMap.put(getId(), this);
    }

    public String getId() {
        return this.section.getName();
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    public int getMaxPlayers() {
        return this.section.getInt("max-players");
    }

    public int getStartThreshold() {
        return this.section.getInt("start-threshold");
    }

    public List<ArenaInfo> getArenaInfos() {
        return arenaInfos;
    }

    public Location loadLobbyLocation() {
        return ConfigHelper.readLocation(this.section.getConfigurationSection("lobby"));
    }

    public List<ArenaInfo> loadArenaInfos() {
        List<ArenaInfo> arenas = new ArrayList<>();

        for (String key : this.section.getConfigurationSection("arenas").getKeys(false)) {
            ArenaInfo info = new ArenaInfo();

            ConfigurationSection arenaSection = this.section
                    .getConfigurationSection("arenas." + key);

            info.setName(key);

            String world = arenaSection.getString("spawn-location.world");
            double x = arenaSection.getDouble("spawn-location.x");
            double y = arenaSection.getDouble("spawn-location.y");
            double z = arenaSection.getDouble("spawn-location.z");

            info.setDefaultLocation(new Location(Bukkit.getWorld(world), x, y, z));
            info.setDefaultTime(arenaSection.getInt("time"));
            info.setCountdown(arenaSection.getInt("countdown"));

            ConfigurationSection dataSection = arenaSection.getConfigurationSection("data");
            info.setData(dataSection);

            arenas.add(info);
        }

        return arenas;
    }

    @Override
    public String toString() {
        return "GameInfo{" +
                "section=" + section +
                ", arenaInfos=" + Arrays.toString(arenaInfos.toArray()) +
                '}';
    }
}
