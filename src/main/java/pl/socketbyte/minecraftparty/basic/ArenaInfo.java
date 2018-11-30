package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ArenaInfo {

    private int countdown;
    private Location defaultLocation;
    private long time;
    private long defaultTime;

    private String name;

    private ConfigurationSection data;

    public void startTimer() {
        this.time = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(this.defaultTime);
    }

    public void setDefaultTime(long defaultTime) {
        this.defaultTime = defaultTime;
    }

    public long getTimeLeft() {
        return (this.time - System.currentTimeMillis() <= 0) ? 0 : this.time - System.currentTimeMillis();
    }

    public long getTimeLeftAsSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(getTimeLeft());
    }

    public boolean isActive() {
        return getTimeLeft() > 0;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public Location getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(Location defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public long getTime() {
        return time;
    }

    public ConfigurationSection getData() {
        return data;
    }

    public void setData(ConfigurationSection data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ArenaInfo{" +
                "countdown=" + countdown +
                ", defaultLocation=" + defaultLocation +
                ", time=" + time +
                ", name='" + name + '\'' +
                ", data=" + data +
                '}';
    }
}
