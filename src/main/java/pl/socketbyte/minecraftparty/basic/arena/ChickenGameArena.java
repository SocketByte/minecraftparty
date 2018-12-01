package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.chickengame.Animal;
import pl.socketbyte.minecraftparty.basic.arena.helper.chickengame.AnimalData;
import pl.socketbyte.minecraftparty.basic.arena.helper.chickengame.AnimalPointData;
import pl.socketbyte.minecraftparty.basic.arena.helper.chickengame.AnimalType;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChickenGameArena extends Arena {

    private int radius;
    private int amount;

    private final List<Animal> animals = new ArrayList<>();
    private final Map<AnimalType, Double> chances = new HashMap<>();
    private final Map<AnimalType, AnimalData> dataMap = new HashMap<>();

    public ChickenGameArena(Game game) {
        super(game);
    }

    public int getSize() {
        return animals.size();
    }

    public int getAnimalsToSpawn() {
        return amount - getSize();
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(), getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {

    }

    @Override
    public void onTick(long timeLeft) {
        if (getAnimalsToSpawn() > 0) {
            spawn(getAnimalsToSpawn());
        }
    }

    public void spawn(int amount) {
        for (int i = 0; i < amount; i++) {
            AnimalType animalType = RandomHelper.pick(chances);
            if (animalType == null)
                continue;

            boolean plus = RandomHelper.chance(85);

            AnimalData data = dataMap.get(animalType);
            Animal animal = new Animal(data);
            animal.setPlus(plus);
            animal.spawn(randomizeLocation());
            animals.add(animal);
        }
    }

    public Location randomizeLocation() {
        Location location = getArenaInfo().getDefaultLocation().clone();

        int randomX = RandomHelper.randomInteger(-radius, radius);
        int randomZ = RandomHelper.randomInteger(-radius, radius);

        location.add(randomX, 0, randomZ);
        return location;
    }


    @Override
    public void onFreeze() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().clear();
        }
        for (Animal animal : animals) {
            animal.kill();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!getGame().isPlaying((Player) event.getWhoClicked()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrop(PlayerDropItemEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!getGame().isPlaying((Player) event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);

        radius = getArenaInfo().getData().getInt("radius");
        amount = getArenaInfo().getData().getInt("amount");

        ConfigurationSection chickenData = getArenaInfo().getData().getConfigurationSection("chicken");
        ConfigurationSection cowData = getArenaInfo().getData().getConfigurationSection("cow");

        pushDataFor(AnimalType.CHICKEN, chickenData);
        pushDataFor(AnimalType.COW, cowData);
    }

    public void pushDataFor(AnimalType type, ConfigurationSection section) {
        AnimalData data = new AnimalData(type);

        AnimalPointData plus = new AnimalPointData();
        plus.setPoints(section.getInt("plus.points"));
        plus.setTitle(MessageHelper.fixColor(section.getString("plus.title")));
        data.setPlus(plus);

        AnimalPointData minus = new AnimalPointData();
        minus.setPoints(section.getInt("minus.points"));
        minus.setTitle(MessageHelper.fixColor(section.getString("minus.title")));
        data.setMinus(minus);

        data.setChance(section.getDouble("chance"));
        data.setHealth(section.getInt("health"));

        chances.put(type, data.getChance());
        dataMap.put(type, data);
    }

    @Override
    public void onStart() {
        for (Player player : getGame().getPlaying()) {
            player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
            player.getInventory().setHeldItemSlot(0);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null)
            return;

        if (!getGame().isPlaying(event.getEntity().getKiller()))
            return;

        if (!getGame().isArena(this))
            return;

        LivingEntity entity = event.getEntity();
        Animal animal = null;
        for (Animal a : animals) {
            if (a.getEntity().equals(entity)) {
                animal = a;
                break;
            }
        }
        if (animal == null)
            return;

        event.setDroppedExp(0);
        event.getDrops().clear();

        Player player = entity.getKiller();
        MessageHelper.send(player, animal.isPlus() ? animal.getData().getPlus().getTitle()
                : animal.getData().getMinus().getTitle());

        if (animal.isPlus())
            addInternalScore(player, animal.getData().getPlus().getPoints());
        else
            addInternalScore(player, animal.getData().getMinus().getPoints());

        animals.remove(animal);
    }

    @Override
    public void onEnd() {

    }
}
