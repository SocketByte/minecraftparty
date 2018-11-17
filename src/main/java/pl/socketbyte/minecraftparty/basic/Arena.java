package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import pl.socketbyte.minecraftparty.MinecraftParty;

import java.util.List;

public abstract class Arena implements Listener {

    private final Game game;
    private final ArenaTask taskManager = new ArenaTask(this);

    private boolean active = false;
    private boolean freeze = false;

    private int id;

    public Arena(Game game) {
        this.game = game;
        register();
        onInit();
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

    protected void start() {
        this.active = true;
        onStart();
    }

    public void end() {
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
    }

    public void unfreeze() {
        this.freeze = false;
    }

    private void register() {
        Bukkit.getPluginManager().registerEvents(this, MinecraftParty.getInstance());
    }

    public ArenaInfo getArenaInfo() {
        return game.getGameInfo().getArenaInfos().get(id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public abstract List<Location> getPlayerStartPositions();

    /**
     * Executed on the arena initialization
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
