package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.board.impl.GameBoard;
import pl.socketbyte.minecraftparty.basic.func.DoubleJump;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.SortHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private boolean occupied = false;
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
        this.lobby.dispose();
        this.board.dispose();
        this.playing.clear();
        this.running = false;
    }

    private void reset() {
        this.lobby.dispose();
        this.board.dispose();
        this.playing.clear();
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

    public boolean isOccupied() {
        return occupied;
    }

    public GameCache getCache() {
        return cache;
    }

    public void join(Player player) {
        this.playing.add(player.getUniqueId());
        this.points.put(player.getUniqueId(), 0);

        cache.addCache(player);

        player.teleport(lobby.getLobbyLocation());

        broadcast(I18n.get().message("player-joined",
                "{PLAYER}", player.getName(),
                "{PLAYERS}", getPlaying().size(),
                "{MAX}", maxPlayers));
    }

    public void leave(Player player, boolean endGame) {
        this.playing.remove(player.getUniqueId());
        this.points.remove(player.getUniqueId());

        cache.apply(player);

        DoubleJump.disable(player);

        currentArena.remove(player);

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

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

    public void broadcastTitle(String header, String sub, int fadeIn, int stay, int fadeOut) {
        for (Player player : getPlaying()) {
            MessageHelper.sendTitle(player, header, sub, fadeIn, stay, fadeOut);
        }
    }

    public void broadcastActionBar(String message) {
        for (Player player : getPlaying()) {
            MessageHelper.sendActionBar(player, message);
        }
    }

    public Map<UUID, Integer> getPoints() {
        return this.points;
    }

    public int getPlaceByScore(int score) {
        int place = 0;
        Map<UUID, Integer> sorted = SortHelper.sortByIntValue(getPoints());
        int index = 0;
        for (int i = 0; i < sorted.size(); i++) {
            int sc = (int) sorted.values().toArray()[i];

            if (sc == score) {
                return i + 1;
            }
        }
        return -1;
    }

    public int getPoints(Player player) {
        return this.points.get(player.getUniqueId());
    }

    public int getPoints(OfflinePlayer player) {
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
        this.occupied = true;

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
    @SuppressWarnings("unchecked")
    protected void end() {
        this.running = false;

        Map<UUID, Integer> sorted = SortHelper.sortByIntValue(getPoints());
        Map.Entry<UUID, Integer> entry = (Map.Entry<UUID, Integer>) sorted.entrySet().toArray()[0];

        OfflinePlayer winner = Bukkit.getOfflinePlayer(entry.getKey());

        for (String str : I18n.get().list("winner-broadcast")) {
            broadcast(str
                    .replace("{PLAYER}", winner.getName())
                    .replace("{SCORE}", String.valueOf(entry.getValue())));
        }

        TaskHelper.delay(() -> {
            for (Player player : getPlaying()) {
                // TODO: 23.10.2018 Assign global XP points or something

                leave(player, true);
            }
            this.currentArena = null;

            // dispose and prepare for next game
            reset();
        }, 5, TimeUnit.SECONDS);
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
        System.out.println("Started arena " + currentArena.getArenaInfo().getName());
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
