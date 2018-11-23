package pl.socketbyte.minecraftparty.basic.board.impl;

import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.PartyBoard;
import pl.socketbyte.minecraftparty.basic.board.Board;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.*;

public class GameBoard implements PartyBoard {

    private final Board board;
    private final Game game;

    public GameBoard(Game game) {
        this.game = game;
        this.board = new Board(I18n.get().message("gameboard-title"));
        init();
    }

    @Override
    public void init() {
        List<Player> players = this.game.getPlaying();

        for (Player player : players) {
            board.setScore(player, this.game.getPoints(player));
            board.show(player);
        }
    }

    @Override
    public void update() {
        List<Player> players = this.game.getPlaying();

        for (Player player : players) {
            board.setScore(player, this.game.getPoints(player));
        }
    }

    @Override
    public void dispose() {
        board.dispose();
    }

}
