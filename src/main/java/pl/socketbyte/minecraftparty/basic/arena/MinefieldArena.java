package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.arena.helper.minefield.Direction;
import pl.socketbyte.minecraftparty.basic.arena.helper.minefield.Mine;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.ConfigHelper;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinefieldArena extends Arena {

    private Location min;
    private Location max;
    private double probablity;

    private Map<Location, Mine> mines = new HashMap<>();

    public MinefieldArena(Game game) {
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
        for (Mine mine : mines.values()) {
            mine.remove();
        }
    }

    @Override
    public void onInit() {
        getBoard().setType(ArenaBoardType.SCORES);

        Location[] minMax = ConfigHelper.readMinMaxLocation(getArenaInfo().getDefaultLocation().getWorld(),
                getArenaInfo().getData().getConfigurationSection("locations"));
        min = minMax[0];
        max = minMax[1];

        probablity = getArenaInfo().getData().getDouble("probability");
    }

    private List<Player> winners = new ArrayList<>();
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        if (isCountdown()) {
            event.setTo(event.getFrom());
            return;
        }

        if (isDisqualified(event.getPlayer()))
            return;

        if (winners.contains(event.getPlayer())) {
            event.setTo(event.getFrom());
            return;
        }

        Player player = event.getPlayer();
        int wholeDistance = max.getBlockX() - min.getBlockX();
        int distance = max.getBlockX() - player.getLocation().getBlockX();
        int points = wholeDistance - distance;

        setInternalScore(player, points);

        if (player.getLocation().getBlockX() > (max.getBlockX() + 1)) {
            getGame().broadcast(I18n.get().message("minefield.win", "{PLAYER}", player.getName()));
            getGame().broadcastActionBar(I18n.get().message("minefield.win", "{PLAYER}", player.getName()));

            addInternalScore(player, 25);
            winners.add(player);
            return;
        }

        Block block = event.getTo().getBlock();
        if (block.getType()
                == Material.STONE_PLATE) {
            System.out.println(mines.containsKey(block.getLocation()));
            if (!mines.containsKey(block.getLocation()))
                return;

            Mine mine = mines.get(block.getLocation());
            mine.remove();

            disqualify(event.getPlayer());
            event.getPlayer().teleport(getArenaInfo().getDefaultLocation().clone().add(0, 15, 0));
            MessageHelper.send(event.getPlayer(), I18n.get().message("minefield.died"));
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

    @Override
    public void onStart() {
        for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
            for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
                if (!RandomHelper.chance(probablity))
                    continue;

                Location location = new Location(getArenaInfo().getDefaultLocation().getWorld(),
                        x, min.getBlockY() - 1, z);
                Mine mine = new Mine(location);
                mine.place();

                mines.put(location, mine);
            }
        }
    }

    @Override
    public void onEnd() {

    }
}
