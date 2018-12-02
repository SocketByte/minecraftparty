package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.horserace.Checkpoint;
import pl.socketbyte.minecraftparty.basic.arena.helper.horserace.RaceHorse;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HorseRaceArena extends Arena {

    private final List<RaceHorse> horses = new ArrayList<>();
    private final Map<Integer, Checkpoint> checkpoints = new HashMap<>();

    public HorseRaceArena(Game game) {
        super(game);
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

    }

    @Override
    public void onFreeze() {
        for (RaceHorse horse : horses) {
            horse.kill();
        }
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);

        ConfigurationSection section = getArenaInfo().getData().getConfigurationSection("checkpoint-locations");
        for (String key : section.getKeys(false)) {
            int id = Integer.valueOf(key);

            Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                    section.getInt(key + ".x"), section.getInt(key + ".y"), section.getInt(key + ".z"));

            Checkpoint checkpoint = new Checkpoint();
            checkpoint.setId(id);
            checkpoint.setLocation(location);

            location.getBlock().setType(Material.STANDING_BANNER);

            checkpoints.put(id, checkpoint);
        }
    }

    @EventHandler
    public void onVehicleMove(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        if (isFreezed() || !isActive())
            return;

        if (isCountdown() && (event.getTo().getX() != event.getFrom().getX()
                || event.getTo().getY() != event.getFrom().getY()
                || event.getTo().getZ() != event.getFrom().getZ())) {
            event.setTo(event.getFrom());
            return;
        }

        Horse horseEntity = (Horse) event.getPlayer().getVehicle();
        RaceHorse horse = null;
        for (RaceHorse h : horses) {
            if (h.getHorse().equals(horseEntity)) {
                horse = h;
                break;
            }
        }
        if (horse == null)
            return;

        int id = horse.getCheckpointId();
        Checkpoint last = checkpoints.get(id);

        Block block = event.getTo().clone().subtract(0, 1, 0).getBlock();
        if (!block.getType().equals(Material.STONE) && block.getType() != Material.AIR) {
            horse.getHorse().teleport(last.getLocation());
            MessageHelper.send(horse.getOwner(), I18n.get().message("horse-race.dont-leave"));
            return;
        }

        Checkpoint next = checkpoints.get(id + 1);
        if (next == null) {
            horse.setCheckpointId(1);
            return;
        }

        Location location = horse.getHorse().getLocation();
        int radius = 5;
        if (location.distanceSquared(next.getLocation()) <= radius * radius) {
            horse.setCheckpointId(next.getId());
            horse.setScoreUntilCheckpoint(getInternalScore(horse.getOwner()));
            return;
        }

        if (last == null)
            last = checkpoints.get(checkpoints.size());

        int wholeDistance = (int) last.getLocation().distance(next.getLocation());
        int distance = wholeDistance - (int) horse.getHorse().getLocation().distance(next.getLocation());
        int scoreUntilCheckpoint = horse.getScoreUntilCheckpoint();

        setInternalScore(horse.getOwner(), scoreUntilCheckpoint + distance);
    }

    @Override
    public void onStart() {
        TaskHelper.sync(() -> {
            for (Player player : getGame().getPlaying()) {
                RaceHorse horse = new RaceHorse(player);
                horse.spawn();

                horses.add(horse);
                System.out.println("Spawned horse!");
            }
        });
    }

    @Override
    public void onEnd() {

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
    public void onVehicleExit(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player))
            return;

        if (!getGame().isPlaying((Player) event.getExited()))
            return;

        if (!getGame().isArena(this))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!getGame().isArena(this))
            return;

        RaceHorse horse = null;
        for (RaceHorse h : horses) {
            if (h.getHorse().equals(event.getEntity())) {
                horse = h;
                break;
            }
        }
        if (horse == null)
            return;

        event.setCancelled(true);
    }

}
