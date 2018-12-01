package pl.socketbyte.minecraftparty.basic.arena.helper.punchthebats;

import org.bukkit.Location;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import pl.socketbyte.minecraftparty.commons.MessageHelper;

import java.util.UUID;

public class ArenaBat {

    private Bat bat;

    private UUID uniqueId;
    private String title;
    private int points;

    public ArenaBat(Location location, String title, int points, int health) {
        this.title = title;
        this.points = points;
        this.uniqueId = spawnBat(location, health);
    }

    public UUID spawnBat(Location location, int health) {
        Bat bat = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
        bat.setCanPickupItems(false);
        bat.setCustomNameVisible(true);
        bat.setCustomName(MessageHelper.fixColor(title));
        bat.setHealth(health);
        bat.setMaxHealth(health);
        this.bat = bat;
        return bat.getUniqueId();
    }

    public void kill() {
        bat.remove();
    }

    public boolean compare(Bat bat) {
        return bat.getUniqueId().equals(uniqueId);
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public String getTitle() {
        return title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
