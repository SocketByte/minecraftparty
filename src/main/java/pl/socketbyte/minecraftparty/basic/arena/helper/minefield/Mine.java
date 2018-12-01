package pl.socketbyte.minecraftparty.basic.arena.helper.minefield;

import org.bukkit.Location;
import org.bukkit.Material;

public class Mine {

    private Location location;

    public Mine(Location location) {
        this.location = location;
    }

    public void place() {
        this.location.getBlock().setType(Material.STONE_PLATE);
    }

    public void remove() {
       this.location.getBlock().setType(Material.AIR);
    }
}
