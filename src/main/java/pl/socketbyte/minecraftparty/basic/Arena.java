package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitTask;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoard;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.SortHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Arena implements Listener {

    private final Game game;
    private final ArenaTask taskManager = new ArenaTask(this);
    private ArenaBoard board;

    private boolean active = false;
    private boolean freeze = false;
    private boolean countdown = false;

    private boolean pvp = false;

    private String fancyName;

    private long realCountdown;

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    /**
     * For storing dead/disqualified players per arena
     */
    private final Map<UUID, Long> disqualifiedPlayers = new HashMap<>();

    public void disqualify(Player player) {
        this.disqualifiedPlayers.put(player.getUniqueId(), System.currentTimeMillis());
        TaskHelper.sync(() -> {
            for (Player playing : getGame().getPlaying()) {
                playing.hidePlayer(player);
            }
            player.setAllowFlight(true);
            player.setFlying(true);
        });
        this.board.update();
    }

    public boolean isDisqualified(Player player) {
        return this.disqualifiedPlayers.containsKey(player.getUniqueId());
    }

    public Map<UUID, Long> getDisqualifiedMap() {
        return disqualifiedPlayers;
    }

    public List<Player> getDisqualifiedPlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : disqualifiedPlayers.keySet()) {
            list.add(Bukkit.getPlayer(uuid));
        }
        return list;
    }

    public int getPlayersLeft() {
        List<Player> players = getGame().getPlaying();
        List<Player> disqualifiedPlayers = getDisqualifiedPlayers();

        return players.size() - disqualifiedPlayers.size();
    }

    public Player getLastPlayer() {
        if (getPlayersLeft() > 1)
            return null;

        List<Player> players = getGame().getPlaying();
        List<Player> disqualifiedPlayers = getDisqualifiedPlayers();

        for (Player player : players) {
            if (disqualifiedPlayers.contains(player)) {
                continue;
            }
            return player;
        }
        return null;
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

    public void remove(Player player) {
        disqualifiedPlayers.remove(player.getUniqueId());
        internalScores.remove(player.getUniqueId());
    }

    public int getPlaceByScore(int score) {
        int place = 0;
        Map<UUID, Integer> sorted = SortHelper.sortByIntValue(getInternalScores());
        int index = 0;
        for (int i = 0; i < sorted.size(); i++) {
            int sc = (int) sorted.values().toArray()[i];

            if (sc == score) {
                return i + 1;
            }
        }
        return -1;
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
        this.fancyName = getFancyName();

        for (Player player : getGame().getPlaying()) {
            this.internalScores.put(player.getUniqueId(), 0);
        }

        this.realCountdown = System.currentTimeMillis() + (getArenaInfo().getCountdown() * 1000);

        this.countdown = true;
        this.board.update();
        this.board.init();

        for (String str : I18n.get().list("arena-start-message")) {
            getGame().broadcast(str
                    .replace("{ARENA}", this.fancyName)
                    .replace("{GOAL}", I18n.get().message(getArenaInfo().getName() + ".goal"))
                    .replace("{TIP}", I18n.get().message(getArenaInfo().getName() + ".tip")));
        }
        onCountdown();

        getGame().broadcastTitle(
                I18n.get().message("arena-start-title", "{ARENA}", this.fancyName),
                I18n.get().message("arena-start-prepare"), 40, getArenaInfo().getCountdown() / 2, 40);
        getTaskManager().schedule(() -> {
            if (!this.countdown)
                return;

            this.board.update();
            getGame().broadcastActionBar(I18n.get().message("arena-bar-countdown",
                    "{SECONDS}", TimeUnit.MILLISECONDS.toSeconds(getRealCountdownTime())));

            if (getRealCountdownTime() <= 0) {
                this.countdown = false;
                getGame().broadcastActionBar(I18n.get().message("arena-bar-started"));
                onStart();

                getArenaInfo().startTimer();

                getTaskManager().schedule(() -> {
                    if (isFreezed())
                        return;

                    onTick(getArenaInfo().getTimeLeftAsSeconds());
                    this.board.update();

                    if (getArenaInfo().getTimeLeftAsSeconds() <= 0) {
                        for (Player player : getGame().getPlaying()) {
                            if (isDisqualified(player)) {
                                continue;
                            }
                            disqualify(player);
                        }

                        endArena();
                    }
                    else if (board.getType() == ArenaBoardType.DISQUALIFICATION) {
                        Player lastPlayer = getLastPlayer();
                        if (lastPlayer == null)
                            return;

                        disqualify(lastPlayer);

                        endArena();
                    }
                }, 1, TimeUnit.SECONDS);
            }
        }, 1, TimeUnit.SECONDS);
    }


    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!getGame().isArena(this))
            return;

        if (pvp)
            return;

        Entity entity = event.getEntity();
        Entity damager = event.getDamager();

        if (!(entity instanceof Player))
            return;

        if (!(damager instanceof Player))
            return;

        Player player = (Player) entity;
        Player attacker = (Player) damager;

        if (!getGame().isPlaying(player))
            return;

        if (!getGame().isPlaying(attacker))
            return;

        event.setCancelled(true);
    }


    private void endArena() {
        for (Player player : getGame().getPlaying()) {
            int place = getPlaceByScore(getInternalScore(player));
            switch (place) {
                case 1:
                    getGame().addPoints(player, 3);
                    break;
                case 2:
                    getGame().addPoints(player, 2);
                    break;
                case 3:
                    getGame().addPoints(player, 1);
                    break;
            }
        }

        freeze();

        for (String str0 : I18n.get().list("arena-end-message")) {
            getGame().broadcast(str0.replace("{ARENA}", this.fancyName));
        }

        String topMessage = getBoard().getType() == ArenaBoardType.SCORES ? "top-message" : "top-disq-message";
        for (Player player : getGame().getPlaying()) {
            for (String str : I18n.get().list(topMessage)) {
                str = replaceTops(str);

                MessageHelper.send(player, str
                        .replace("{PLACE}", String.valueOf(getPlaceByScore(getInternalScore(player))))
                        .replace("{SCORE}", String.valueOf(getInternalScore(player))));
            }
        }

        getTaskManager().delay(() -> {
            for (Player player : getGame().getPlaying()) {
                for (String str : I18n.get().list("top-total-message")) {
                    str = replaceGameTops(str);

                    MessageHelper.send(player, str
                            .replace("{SCORE}", String.valueOf(getGame().getPoints(player)))
                            .replace("{PLACE}", String.valueOf(getGame().getPlaceByScore(getGame().getPoints(player)))));
                }
            }

            getTaskManager().delay(() -> {
                end();

                TaskHelper.sync(() -> {
                    for (Player playing : getGame().getPlaying()) {
                        for (Player player : getDisqualifiedPlayers()) {
                            playing.showPlayer(player);
                            player.setFlying(false);
                            player.setAllowFlight(false);
                        }
                    }
                });
            }, 5, TimeUnit.SECONDS);
        }, 5, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    protected String replaceGameTops(String str) {
        for (int i = 0; i < 3; i++) {
            int place = i + 1;
            Map<UUID, Integer> sorted = SortHelper.sortByIntValue(this.getGame().getPoints());
            if (i >= sorted.size()) {
                str = str.replace("{TOP-" + place + "}",
                        I18n.get().message("top-total-none",
                                "{PLACE}", String.valueOf(place)));
                continue;
            }
            Map.Entry<UUID, Integer> entry = (Map.Entry<UUID, Integer>)
                    sorted.entrySet().toArray()[i];

            OfflinePlayer plr = Bukkit.getOfflinePlayer(entry.getKey());

            str = str.replace("{TOP-" + place + "}",
                    I18n.get().message("top-total-format",
                            "{PLACE}", String.valueOf(place),
                            "{SCORE}", String.valueOf(getGame().getPoints(plr)),
                            "{PLAYER}", plr.getName()));
        }
        return str;
    }

    @SuppressWarnings("unchecked")
    public String replaceTops(String str) {
        switch (getBoard().getType()) {
            case DISQUALIFICATION: {
                for (int i = 0; i < 3; i++) {
                    int place = i + 1;
                    Map<UUID, Long> sorted = SortHelper.sortByLongValue(getDisqualifiedMap());
                    if (i >= sorted.size()) {
                        str = str.replace("{TOP-" + place + "}",
                                I18n.get().message("top-none",
                                        "{PLACE}", String.valueOf(place)));
                        continue;
                    }
                    Map.Entry<UUID, Integer> entry = (Map.Entry<UUID, Integer>)
                            sorted.entrySet().toArray()[i];

                    OfflinePlayer plr = Bukkit.getOfflinePlayer(entry.getKey());

                    str = str.replace("{TOP-" + place + "}",
                            I18n.get().message("top-disq-format",
                                    "{PLACE}", String.valueOf(place),
                                    "{PLAYER}", plr.getName()));
                }
                break;
            }
            case SCORES: {
                for (int i = 0; i < 3; i++) {
                    int place = i + 1;
                    Map<UUID, Integer> sorted = SortHelper.sortByIntValue(getInternalScores());
                    if (i >= sorted.size()) {
                        str = str.replace("{TOP-" + place + "}",
                                I18n.get().message("top-none",
                                        "{PLACE}", String.valueOf(place)));
                        continue;
                    }
                    Map.Entry<UUID, Integer> entry = (Map.Entry<UUID, Integer>)
                            sorted.entrySet().toArray()[i];

                    OfflinePlayer plr = Bukkit.getOfflinePlayer(entry.getKey());

                    str = str.replace("{TOP-" + place + "}",
                            I18n.get().message("top-format",
                                    "{PLACE}", String.valueOf(place),
                                    "{PLAYER}", plr.getName(),
                                    "{SCORE}", String.valueOf(entry.getValue())));
                }
                break;
            }
        }
        return str;
    }

    private String getFancyName() {
        String id = getArenaInfo().getName();

        id = id.replace("-", " ");
        id = MessageHelper.capitalize(id);

        return id;
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
        if (!game.isPlaying(event.getPlayer()))
            return;

        if (!game.isArena(this))
            return;

        if (isFreezed() && (event.getTo().getX() != event.getFrom().getX()
                || event.getTo().getY() != event.getFrom().getY()
                || event.getTo().getZ() != event.getFrom().getZ())) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!game.isPlaying((Player) event.getEntity()))
            return;

        if (!game.isArena(this))
            return;

        event.setCancelled(true);
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
