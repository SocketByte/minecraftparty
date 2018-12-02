package pl.socketbyte.minecraftparty.basic.board;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import pl.socketbyte.minecraftparty.commons.MessageHelper;

public class Board {

    private final Scoreboard scoreboard;

    private String title;
    private Objective objective;

    public Board(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = title;
        this.objective = createObjective();
    }

    private Objective createObjective() {
        Objective objective = this.scoreboard.registerNewObjective("score", "dummy");
        objective.setDisplayName(MessageHelper.fixColor(this.title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }

    public void updateTitle(String title) {
        try {
            this.title = title;
            this.objective.setDisplayName(MessageHelper.fixColor(this.title));
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        } catch (Exception e) { }

    }

    public void setScore(String entry, int score) {
        this.objective.getScore(MessageHelper.fixColor(entry)).setScore(score);
    }

    public void setScore(Player player, int score) {
        this.objective.getScore(player.getName()).setScore(score);
    }

    public void show(Player player) {
        player.setScoreboard(this.scoreboard);
    }

    public void delete(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void reset(Player player) {
        this.scoreboard.resetScores(player.getName());
    }

    public void dispose() {
        this.objective.unregister();
    }
}
