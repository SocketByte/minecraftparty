package pl.socketbyte.minecraftparty.basic.arena.helper.chickengame;

import org.bukkit.Location;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class Animal {

    private final AnimalData data;
    private LivingEntity entity;
    private boolean plus;

    public Animal(AnimalData data) {
        this.data = data;
    }

    public AnimalData getData() {
        return data;
    }

    public boolean isPlus() {
        return plus;
    }

    public void setPlus(boolean plus) {
        this.plus = plus;
    }

    public void spawn(Location location) {
        switch (data.getType()) {
            case COW: {
                Cow cow = (Cow) location.getWorld().spawnEntity(location, EntityType.COW);
                if (plus) {
                    cow.setCustomName(data.getPlus().getTitle());
                }
                else cow.setCustomName(data.getMinus().getTitle());
                cow.setHealth(data.getHealth());
                cow.setMaxHealth(data.getHealth());
                cow.setCustomNameVisible(true);
                this.entity = cow;
                break;
            }
            case CHICKEN: {
                Chicken chicken = (Chicken) location.getWorld().spawnEntity(location, EntityType.CHICKEN);
                if (plus) {
                    chicken.setCustomName(data.getPlus().getTitle());
                }
                else chicken.setCustomName(data.getMinus().getTitle());
                chicken.setHealth(data.getHealth());
                chicken.setMaxHealth(data.getHealth());
                chicken.setCustomNameVisible(true);
                this.entity = chicken;
                break;
            }
        }
    }

    public void kill() {
        this.entity.remove();
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public void setEntity(LivingEntity entity) {
        this.entity = entity;
    }
}
