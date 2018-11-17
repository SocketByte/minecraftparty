package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ArenaInfo {

    private int countdown;
    private Location defaultLocation;
    private long time;

    private String name;

    private Map<String, Object> data = new HashMap<>();

    public void setTime(long time) {
        this.time = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(time);
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

    public Map<String, Object> getData() {
        return data;
    }

    public void addData(String key, Object object) {
        this.data.put(key, object);
    }

    public void setData(Map<String, Object> data) {
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
