package pl.socketbyte.minecraftparty.basic;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {

    private final Location location;
    private final int food;
    private final double health;
    private final double maxHealth;
    private final int level;
    private final float exp;
    private final float flySpeed;
    private final float walkSpeed;
    private final int fireTicks;
    private final int inventorySlot;
    private final GameMode gameMode;
    private final ItemStack[] contents;
    private final ItemStack[] armorContents;
    private final List<PotionEffect> effects;

    public UserInfo(Player player) {
        this.location = player.getLocation();
        this.food = player.getFoodLevel();
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.level = player.getLevel();
        this.exp = player.getExp();
        this.flySpeed = player.getFlySpeed();
        this.walkSpeed = player.getWalkSpeed();
        this.fireTicks = player.getFireTicks();
        this.inventorySlot = player.getInventory().getHeldItemSlot();
        this.gameMode = player.getGameMode();
        this.contents = player.getInventory().getContents();
        this.armorContents = player.getInventory().getArmorContents();
        this.effects = new ArrayList<>(player.getActivePotionEffects());
        resetStats(player);
    }

    protected void resetStats(Player player) {
        TaskHelper.sync(() -> {
            player.setLevel(0);
            player.setExp(0);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setMaxHealth(20);
            player.setFireTicks(0);
            for (PotionEffect effect : effects) {
                player.removePotionEffect(effect.getType());
            }
            player.setFlySpeed(0.2f);
            player.setWalkSpeed(0.2f);
            player.getInventory().clear();
            player.getInventory().setBoots(new ItemStack(Material.AIR));
            player.getInventory().setHelmet(new ItemStack(Material.AIR));
            player.getInventory().setChestplate(new ItemStack(Material.AIR));
            player.getInventory().setLeggings(new ItemStack(Material.AIR));
            player.setGameMode(GameMode.SURVIVAL);
        });
    }

    public void apply(Player player) {
       TaskHelper.sync(() -> {
           player.setLevel(this.level);
           player.setExp(this.exp);
           player.setFoodLevel(this.food);
           player.setHealth(this.health);
           player.setMaxHealth(this.maxHealth);
           player.setFireTicks(this.fireTicks);
           for (PotionEffect effect : effects) {
               player.addPotionEffect(effect);
           }
           player.setWalkSpeed(this.walkSpeed);
           player.setFlySpeed(this.flySpeed);
           player.setGameMode(this.gameMode);
           player.getInventory().setHeldItemSlot(this.inventorySlot);
           player.getInventory().setContents(this.contents);
           player.getInventory().setArmorContents(this.armorContents);
       });
    }

    public Location getLocation() {
        return location;
    }

    public int getFood() {
        return food;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public int getLevel() {
        return level;
    }

    public double getExp() {
        return exp;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }
}
