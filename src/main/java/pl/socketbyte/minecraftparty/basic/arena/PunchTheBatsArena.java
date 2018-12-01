package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.punchthebats.ArenaBat;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.basic.func.DoubleJump;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PunchTheBatsArena extends Arena {
    public PunchTheBatsArena(Game game) {
        super(game);
    }

    private final Map<UUID, ArenaBat> batMap = new ConcurrentHashMap<>();
    private final Map<Integer, String> titles = new HashMap<>();
    private final Map<Integer, Double> chances = new HashMap<>();
    private final Map<Integer, Integer> health = new HashMap<>();

    private int maxX = 0;
    private int maxY = 0;
    private int maxZ = 0;

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(), getGame().getPlaying().size());
    }

    public int getBatsIdealCount() {
        return (int) getArenaInfo().getData().get("bats");
    }

    public int getBats() {
        return batMap.size();
    }

    public int getAmountToSpawn() {
        return getBatsIdealCount() - getBats();
    }

    @Override
    public void onCountdown() {
    }

    @Override
    public void onTick(long timeLeft) {
        if (getAmountToSpawn() > 0) {
            spawnBats(getAmountToSpawn());
        }
    }

    public void spawnBats(int amount) {
        for (int i = 0; i < amount; i++) {
            int pick = RandomHelper.pick(chances);
            if (pick == 0) {
                continue;
            }
            String title = titles.get(pick);

            ArenaBat bat = new ArenaBat(randomizeLocation(), title, pick, health.get(pick));
            batMap.put(bat.getUniqueId(), bat);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(final EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!getGame().isPlaying((Player) e.getEntity()))
            return;

        if (!getGame().isArena(this))
            return;
        Player p = (Player) e.getEntity();
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    public Location randomizeLocation() {
        Location location = getArenaInfo().getDefaultLocation().clone();

        int randomX = RandomHelper.randomInteger(-maxX, maxX);
        int randomY = RandomHelper.randomInteger(0, maxY);
        int randomZ = RandomHelper.randomInteger(-maxZ, maxZ);

        location.add(randomX, randomY, randomZ);
        return location;
    }

    @Override
    public void onFreeze() {
        DoubleJump.disable(getGame().getPlaying());
        for (ArenaBat bat : batMap.values()) {
            bat.kill();
        }
    }

    @Override
    public void onInit() {
        this.getBoard().setType(ArenaBoardType.SCORES);

        ConfigurationSection titles = getArenaInfo().getData().getConfigurationSection("bat-types");
        ConfigurationSection chances = getArenaInfo().getData().getConfigurationSection("bat-chances");
        ConfigurationSection health = getArenaInfo().getData().getConfigurationSection("bat-health");

        for (String key : titles.getKeys(false)) {
            int points = Integer.valueOf(key);

            this.titles.put(points, titles.getString(key));
        }

        for (String key : chances.getKeys(false)) {
            int points = Integer.valueOf(key);

            this.chances.put(points, chances.getDouble(key));
        }

        for (String key : health.getKeys(false)) {
            int points = Integer.valueOf(key);

            this.health.put(points, health.getInt(key));
        }


        ConfigurationSection randomize = getArenaInfo().getData().getConfigurationSection("bat-loc-randomize");
        this.maxX = randomize.getInt("max-x");
        this.maxY = randomize.getInt("max-y");
        this.maxZ = randomize.getInt("max-z");
    }

    @Override
    public void onStart() {
        DoubleJump.enable(getGame().getPlaying());
    }

    @Override
    public void onEnd() {

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!getGame().isArena(this))
            return;

        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (entity == null || killer == null)
            return;

        if (!(entity instanceof Bat))
            return;

        if (!getGame().isPlaying(killer))
            return;

        Bat bat = (Bat) entity;
        if (!batMap.containsKey(bat.getUniqueId()))
            return;

        ArenaBat arenaBat = batMap.get(bat.getUniqueId());

        if (arenaBat.compare(bat)) {
            addInternalScore(killer, arenaBat.getPoints());
            MessageHelper.send(killer, I18n.get().message("punch-the-bats.killed",
                    "{WORTH}", titles.get(arenaBat.getPoints())));
            event.setDroppedExp(0);
            event.getDrops().clear();
            batMap.remove(arenaBat.getUniqueId());
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
}
