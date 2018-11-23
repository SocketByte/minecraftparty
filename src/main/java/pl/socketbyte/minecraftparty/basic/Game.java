package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.board.impl.GameBoard;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.*;

public class Game extends Thread implements Listener {

    private static GameController controller;

    public static GameController getController() {
        if (controller == null) {
            controller = new GameController();
        }
        return controller;
    }

    private volatile boolean running = true;

    private final String id;

    private Lobby lobby;
    private GameCache cache;
    private GameBoard board;

    private final int maxPlayers;
    private final List<UUID> playing = new ArrayList<>();
    private final Map<UUID, Integer> points = new HashMap<>();

    private final List<Arena> arenas = new ArrayList<>();

    private Arena currentArena;
    private int currentArenaIndex = 0;
    private int maxArenaIndex = 0;

    private final GameInfo info;

    public Game(String id, int maxPlayers) {
        this.id = id;
        this.info = GameInfo.get(this.id);
        this.lobby = new Lobby(this);
        this.cache = new GameCache();
        TaskHelper.sync(() -> {
            this.board = new GameBoard(this);
        });
        this.maxPlayers = maxPlayers;
        this.setName(this.id);
        init();
    }

    public GameInfo getGameInfo() {
        return info;
    }

    public void dispose() {
        this.running = false;
        this.lobby.dispose();
        this.board.dispose();
        this.currentArena = null;
        this.playing.clear();
        reset();
    }

    private void reset() {
        Game.getController().remove(this);
        Game.getController().create(this.id);
    }

    public int getArenas() {
        return arenas.size();
    }

    public int getCurrentArenaIndex() {
        return currentArenaIndex + 1;
    }

    public Arena getCurrentArena() {
        return currentArena;
    }

    public GameBoard getBoard() {
        return board;
    }

    public void addArena(Arena arena) {
        arena.setId(this.arenas.size());
        this.arenas.add(arena);
        this.maxArenaIndex++;
    }

    private void init() {
        Bukkit.getPluginManager().registerEvents(this, MinecraftParty.getInstance());

        lobby.start();
    }

    public GameCache getCache() {
        return cache;
    }

    public void join(Player player) {
        this.playing.add(player.getUniqueId());
        this.points.put(player.getUniqueId(), 0);

        cache.addPositionCache(player);
        cache.addInventoryCache(player);

        player.teleport(lobby.getLobbyLocation());

        broadcast(I18n.get().message("player-joined",
                "{PLAYER}", player.getName(),
                "{PLAYERS}", getPlaying().size(),
                "{MAX}", maxPlayers));
    }

    public void leave(Player player, boolean endGame) {
        this.playing.remove(player.getUniqueId());
        this.points.remove(player.getUniqueId());

        cache.giveItemsBack(player);
        cache.teleportBack(player);

        if (!endGame) {
            broadcast(I18n.get().message("player-left",
                    "{PLAYER}", player.getName(),
                    "{PLAYERS}", getPlaying().size(),
                    "{MAX}", maxPlayers));
        }
    }

    public void broadcast(String message) {
        for (Player player : getPlaying()) {
            MessageHelper.send(player, message);
        }
    }

    public int getPoints(Player player) {
        return this.points.get(player.getUniqueId());
    }

    public void addPoints(Player player, int points) {
        this.points.put(player.getUniqueId(), this.points.get(player.getUniqueId()) + points);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (getPlaying().contains(event.getPlayer())) {
            // player left the game during the match!
            leave(event.getPlayer(), false);
        }
    }

    @Override
    public void run() {
        this.board.init();
        startNewArena();

        while (running) {
            if (!currentArena.isActive() && !currentArena.isFreezed()) {
                this.board.update();
                next();
            }
        }
    }

    /**
     * Called when all arenas are finished
     */
    protected void end() {
        // TODO: 23.10.2018 Broadcast some winning info etc
        broadcast("Game ended!");

        for (Player player : getPlaying()) {
            // TODO: 23.10.2018 Assign global XP points or something

            leave(player, true);
        }

        // dispose and prepare for next game
        dispose();
    }

    public void next() {
        currentArenaIndex++;
        if (currentArenaIndex == maxArenaIndex) {
            end();
            return;
        }

        currentArena.end();
        startNewArena();
    }

    private void startNewArena() {
        currentArena = arenas.get(currentArenaIndex);

        teleportToLocations();
        currentArena.start();
    }

    private void teleportToLocations() {
        int currentLoc = 0;
        for (Player player : getPlaying()) {
            Location location = currentArena.getPlayerStartPositions().get(currentLoc);
            player.getInventory().clear();
            player.teleport(location);
            currentLoc++;
        }
    }

    public List<Player> getPlaying() {
        List<Player> players = new ArrayList<>();
        for (UUID uniqueId : playing)
            players.add(Bukkit.getPlayer(uniqueId));
        return players;
    }

    public boolean isArena(Arena arena) {
        if (currentArena == null)
            return false;

        return currentArena.getArenaInfo().getName()
                .equals(arena.getArenaInfo().getName());
    }

    public boolean isPlaying(Player player) {
        return playing.contains(player.getUniqueId());
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
