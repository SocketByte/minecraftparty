package pl.socketbyte.minecraftparty.basic.arena.helper.horserace;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RaceHorse {

    private Player owner;
    private Horse horse;

    private int checkpointId;
    private int scoreUntilCheckpoint;

    public RaceHorse(Player player) {
        this.owner = player;
    }

    public void spawn() {
        Horse horse = (Horse) this.owner.getLocation().getWorld()
                .spawnEntity(this.owner.getLocation(), EntityType.HORSE);
        horse.setPassenger(this.owner);
        horse.setTamed(true);
        horse.setAdult();
        horse.setMaxHealth(1000);
        horse.setHealth(1000);
        horse.setOwner(owner);
        horse.setStyle(Horse.Style.BLACK_DOTS);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        this.horse = horse;
    }

    public int getScoreUntilCheckpoint() {
        return scoreUntilCheckpoint;
    }

    public void setScoreUntilCheckpoint(int scoreUntilCheckpoint) {
        this.scoreUntilCheckpoint = scoreUntilCheckpoint;
    }

    public void addScoreUntilCheckpoint(int scoreUntilCheckpoint) {
        this.scoreUntilCheckpoint += scoreUntilCheckpoint;
    }

    public int getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointId(int checkpointId) {
        this.checkpointId = checkpointId;
    }

    public void kill() {
        this.horse.remove();
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Horse getHorse() {
        return horse;
    }

    public void setHorse(Horse horse) {
        this.horse = horse;
    }
}

