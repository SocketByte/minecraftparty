package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoard;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Arena implements Listener {

    private final Game game;
    private final ArenaTask taskManager = new ArenaTask(this);
    private ArenaBoard board;

    private boolean active = false;
    private boolean freeze = false;
    private boolean countdown = false;

    private long realCountdown;

    /**
     * For storing dead/disqualified players per arena
     */
    private final List<UUID> disqualifiedPlayers = new ArrayList<>();

    public void disqualify(Player player) {
        this.disqualifiedPlayers.add(player.getUniqueId());
        this.board.update();
    }

    public boolean isDisqualified(Player player) {
        return this.disqualifiedPlayers.contains(player.getUniqueId());
    }

    public List<Player> getDisqualifiedPlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : disqualifiedPlayers) {
            list.add(Bukkit.getPlayer(uuid));
        }
        return list;
    }

    /**
     * For storing internal points per arena
     */
    private final Map<UUID, Integer> internalScores = new HashMap<>();

    public void addInternalScore(Player player, int points) {
        this.internalScores.put(player.getUniqueId(), this.internalScores.get(player.getUniqueId()) + points);
        this.board.update();
    }

    public int getInternalScore(Player player) {
        return this.internalScores.get(player.getUniqueId());
    }

    public Map<UUID, Integer> getInternalScores() {
        return this.internalScores;
    }

    private int id;

    public Arena(Game game) {
        register();
        this.game = game;
        TaskHelper.sync(() -> {
            this.board = new ArenaBoard(this.game, this);
            onInit();
        });
    }

    public ArenaBoard getBoard() {
        return board;
    }

    public Game getGame() {
        return game;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFreezed() {
        return freeze;
    }

    public ArenaTask getTaskManager() {
        return taskManager;
    }

    public boolean isCountdown() {
        return countdown;
    }

    protected void start() {
        this.active = true;

        for (Player player : getGame().getPlaying()) {
            this.internalScores.put(player.getUniqueId(), 0);
        }

        this.realCountdown = System.currentTimeMillis() + (getArenaInfo().getCountdown() * 1000);

        this.countdown = true;
        this.board.update();
        this.board.init();

        onCountdown();
        getTaskManager().schedule(() -> {
            if (!this.countdown)
                return;

            this.board.update();

            if (getRealCountdownTime() <= 0) {
                this.countdown = false;
                onStart();

                getArenaInfo().startTimer();

                getTaskManager().schedule(() -> {
                    if (isFreezed())
                        return;

                    if (getArenaInfo().getTimeLeftAsSeconds() <= 0) {
                        freeze();
                        getTaskManager().delay(this::end, 5, TimeUnit.SECONDS);
                        return;
                    }

                    onTick(getArenaInfo().getTimeLeftAsSeconds());
                    this.board.update();
                }, 1, TimeUnit.SECONDS);
            }
        }, 1, TimeUnit.SECONDS);
    }

    public long getRealCountdownTime() {
        return this.realCountdown - System.currentTimeMillis();
    }

    public void end() {
        this.freeze = false;
        this.active = false;
        this.taskManager.dispose();
        onEnd();
    }

    /**
     * Freezes the timer, it's for freezing the time during arena end-effects,
     * to interrupt Game object from moving onto the next arena
     */
    public void freeze() {
        this.freeze = true;
        this.board.dispose();
        onFreeze();

        getGame().getBoard().init();
    }

    public void unfreeze() {
        this.freeze = false;
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(this, MinecraftParty.getInstance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isFreezed() && (event.getTo().getX() != event.getFrom().getX()
                || event.getTo().getY() != event.getFrom().getY()
                || event.getTo().getZ() != event.getFrom().getZ())) {
            event.setTo(event.getFrom());
        }
    }

    public ArenaInfo getArenaInfo() {
        return game.getGameInfo().getArenaInfos().get(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract List<Location> getPlayerStartPositions();

    /**
     * Executed when arena is in countdown state before onStart()
     */
    public abstract void onCountdown();

    /**
     * Executed each second when arena is activated
     */
    public abstract void onTick(long timeLeft);

    /**
     * Executed on arena freeze
     */
    public abstract void onFreeze();

    /**
     * Executed on the arena initialization, runs synchronously
     */
    public abstract void onInit();

    /**
     * Executed on the arena startup, when the players are starting the actual game
     */
    public abstract void onStart();

    /**
     * Executed on the arena end, when the time ends or end() method is invoked
     * This executes just before the new arena is created
     *
     * You should reset the arena blocks etc here
     */
    public abstract void onEnd();


}
