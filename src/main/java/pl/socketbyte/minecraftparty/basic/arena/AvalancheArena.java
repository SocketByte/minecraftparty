package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AvalancheArena extends Arena {
    private int baseTime;
    private int timePassed;
    private boolean waveInProgress;
    private int countdownTime;

    private Location min;
    private Location max;

    private Map<UUID, Snowball> balls = new HashMap<>();

    public AvalancheArena(Game game) {
        super(game);
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(), getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {

    }

    private Location slabLocation;

    @Override
    public void onTick(long timeLeft) {
        if (waveInProgress) {
            for (Player player : getGame().getPlaying()) {
                player.setLevel(countdownTime);
            }
            countdownTime--;
        }

        nextWave();
    }

    public void nextWave() {
        if (waveInProgress)
            return;
        waveInProgress = true;

        countdownTime = baseTime;

        place();

        TaskHelper.delay(() -> {
            for (Player player : getGame().getPlaying()) {
                player.closeInventory();
                player.setLevel(0);
            }

            // avalanche fall
            spawnSnowballs();

            TaskHelper.delay(() -> {
                for (Player player : getGame().getPlaying()) {
                    Location location = player.getLocation();
                    if (location.getBlockX() != slabLocation.getBlockX() || location.getBlockZ() != slabLocation.getBlockZ()) {
                        disqualify(player);
                        player.teleport(getArenaInfo().getDefaultLocation().clone().add(0, 10, 0));

                        setInternalScore(player, getInternalScore(player) - 1);
                        System.out.println("Hit!");
                    }
                }

                TaskHelper.delay(() -> {
                    if (baseTime > 1) {
                        baseTime--;
                        if (baseTime == 1) {
                            setPvp(true);
                            setInfiniteHealth(false);
                        }
                    }
                    waveInProgress = false;
                    // on wave reset

                    balls.clear();
                    remove();
                }, 5, TimeUnit.SECONDS);
            }, 3, TimeUnit.SECONDS);
        }, baseTime + 1, TimeUnit.SECONDS);
    }

    public void place() {
        int y = min.getBlockY();
        int x = RandomHelper.randomInteger(min.getBlockX(), max.getBlockX());
        int z = RandomHelper.randomInteger(min.getBlockZ(), max.getBlockZ());

        Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(), x, y, z);
        location.getBlock().setType(Material.WOOD_STEP);

        this.slabLocation = location;
    }

    public void remove() {
        this.slabLocation.getBlock().setType(Material.AIR);
    }

    public void spawnSnowballs() {
        System.out.println("Spawned!");
        for (double x = min.getX(); x < max.getX(); x++) {
            for (double z = min.getZ(); z < max.getZ(); z++) {
                Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                        x + RandomHelper.randomDouble(-0.3, 0.3), min.getY() + 50,
                        z + RandomHelper.randomDouble(-0.3, 0.3));

                Snowball ball = (Snowball) getArenaInfo().getDefaultLocation()
                        .getWorld().spawnEntity(location, EntityType.SNOWBALL);
                ball.setVelocity(new Vector(0, RandomHelper.randomDouble(-0.5, -0.3), 0));
                ball.setBounce(false);
                balls.put(ball.getUniqueId(), ball);
            }
        }
    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player))
            return;

        if (isFreezed() || !isActive())
            return;

        Player player = (Player) entity;
        if (!getGame().isPlaying(player))
            return;

        if (!getGame().isArena(this))
            return;

        if (event.getDamager().getType() != EntityType.SNOWBALL)
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        Player player = event.getPlayer();

        event.setCancelled(true);
    }

    @Override
    public void onFreeze() {

    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);

        Location[] locations = ConfigHelper.readMinMaxLocation(
                getArenaInfo().getDefaultLocation().getWorld(),
                getArenaInfo().getData().getConfigurationSection("locations"));
        this.min = locations[0];
        this.max = locations[1];
        baseTime = getArenaInfo().getData().getInt("baseTime");
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onEnd() {

    }
}
