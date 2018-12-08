package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.concurrent.TimeUnit;

public class Lobby extends Thread implements Listener {

    private Game game;
    private Location lobbyLocation;

    private volatile boolean running = true;

    public Lobby(Game game) {
        this.game = game;
        this.lobbyLocation = game.getGameInfo().getLobbyLocation();
        Bukkit.getPluginManager().registerEvents(this, MinecraftParty.getInstance());
    }

    public void setLobbyLocation(Location lobbyLocation) {
        this.lobbyLocation = lobbyLocation;
    }

    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    @Override
    public void run() {
        try {
            while (running && getPlayersNeeded() > 0) {
                Thread.sleep(5000);
                if (getPlayersNeeded() > 0) {
                    game.broadcast(I18n.get().message("waiting-for-players",
                            "{PLAYERS}", getPlayersNeeded()));
                }
            }
            // ye ye, I know, but it's easier in this case ;/
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 20));
            Thread.sleep(5000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 15));
            Thread.sleep(5000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 10));
            Thread.sleep(5000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 5));
            Thread.sleep(1000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 4));
            Thread.sleep(1000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 3));
            Thread.sleep(1000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 2));
            Thread.sleep(1000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 1));
            Thread.sleep(1000);

            game.start();
            dispose();
        } catch (InterruptedException ignored) {
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!running)
            return;

        if (!game.isPlaying(event.getPlayer()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!running)
            return;

        if (!game.isPlaying((Player) event.getEntity()))
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!running)
            return;

        if (!(event.getEntity() instanceof Player))
            return;

        if (!game.isPlaying((Player) event.getEntity()))
            return;

        event.setCancelled(true);
    }

    public int getPlayersNeeded() {
        return game.getGameInfo().getStartThreshold() - game.getPlaying().size();
    }

    public void dispose() {
        this.running = false;
    }

    public Game getGame() {
        return game;
    }
}
