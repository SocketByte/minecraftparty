package pl.socketbyte.minecraftparty.basic.arena.helper.trampolinio;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;

public class Baloon {

    private Location location;
    private ArmorStand block;

    private final BaloonData data;

    public Baloon(BaloonData data) {
        this.data = data;
    }

    public void place() {
//        this.block = location.getWorld().spawnFallingBlock(location,
////                data.getType().getMaterial(),
////                data.getType().getData());
        //this.block = BaloonEntity.spawn(this.location, data.getType().getMaterial().getId(), data.getType().getData(), data.getTitle());
        this.block = (ArmorStand) this.location.getWorld().spawnEntity(this.location, EntityType.ARMOR_STAND);
        this.block.setVisible(false);
        this.block.setHelmet(new ItemStack(data.getType().getMaterial(), 1, data.getType().getData()));
        this.block.setCustomName(data.getTitle());
        this.block.setCustomNameVisible(true);
        this.block.setGravity(false);
    }

    public BaloonData getData() {
        return data;
    }

    public ArmorStand getBlock() {
        return block;
    }

    public void teleport() {
       ///this.block.setVelocity(new Vector(0, 0.0000000000000001, 0));
        //this.block.teleport(this.location);
    }

    public void kill() {
        this.block.remove();
    }

    public boolean isColliding(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double bX = this.location.getX();
        double bY = this.location.getY();
        double bZ = this.location.getZ();

        double treshold = 0.6;

        return distCompare(x, bX, treshold) && distCompare(y, bY, treshold) && distCompare(z, bZ, treshold);
    }

    public boolean distCompare(double d, double d2, double dist) {
        return d >= d2 - dist && d <= d2 + dist;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
