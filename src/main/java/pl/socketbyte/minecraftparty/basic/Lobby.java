package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Location;
import pl.socketbyte.minecraftparty.commons.io.I18n;

public class Lobby extends Thread {

    private Game game;
    private Location lobbyLocation;

    private volatile boolean running = true;

    public Lobby(Game game) {
        this.game = game;
        this.lobbyLocation = game.getGameInfo().getLobbyLocation();
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
                game.broadcast(I18n.get().message("waiting-for-players",
                        "{PLAYERS}", getPlayersNeeded()));
            }
            // ye ye, I know, but it's easier in this case ;/
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 15));
            Thread.sleep(5000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 10));
            Thread.sleep(5000);
            game.broadcast(I18n.get().message("game-starts", "{TIME}", 5));
            Thread.sleep(2000);
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
