package pl.socketbyte.minecraftparty.basic.arena.helper.horserace;

import org.bukkit.Location;

public class Checkpoint {

    private Location location;
    private int id;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
