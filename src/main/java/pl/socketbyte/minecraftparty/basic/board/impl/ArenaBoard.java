package pl.socketbyte.minecraftparty.basic.board.impl;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.Board;
import pl.socketbyte.minecraftparty.basic.board.PartyBoard;
import pl.socketbyte.minecraftparty.commons.TimeHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.List;

public class ArenaBoard implements PartyBoard {

    private final Board board;
    private final Game game;
    private final Arena arena;

    private ArenaBoardType type;

    public ArenaBoard(Game game, Arena arena) {
        this.game = game;
        this.arena = arena;

        this.board = new Board("<none>");
    }

    public ArenaBoardType getType() {
        return type;
    }

    public void setType(ArenaBoardType type) {
        this.type = type;
    }

    @Override
    public void init() {
        List<Player> players = this.game.getPlaying();

        for (Player player : players) {
            board.show(player);
        }
    }

    @Override
    public void update() {
        List<Player> players = this.game.getPlaying();

        String time = TimeHelper.getFormattedTime(
                arena.getArenaInfo().getTimeLeft());
        if (arena.isCountdown()) {
            time = ChatColor.RED + TimeHelper.getFormattedTime(arena.getRealCountdownTime());
        }

        String formattedTitle = I18n.get().message("arenaboard-title");
        formattedTitle = formattedTitle.replace("{ARENA}", String.valueOf(game.getCurrentArenaIndex()));
        formattedTitle = formattedTitle.replace("{ALL}", String.valueOf(game.getArenas()));
        formattedTitle = formattedTitle.replace("{TIME}", time);

        this.board.updateTitle(formattedTitle);

        for (Player player : players) {
            switch (type) {
                case DISQUALIFICATION: {
                    board.setScore(I18n.get().message("arenaboard-disq-info"),
                            (this.game.getPlaying().size() -
                                    this.arena.getDisqualifiedPlayers().size()));
                }
                case SCORES: {
                    board.setScore(player, arena.getInternalScore(player));
                }
            }
        }
    }

    @Override
    public void dispose() {
        board.dispose();
    }
}
