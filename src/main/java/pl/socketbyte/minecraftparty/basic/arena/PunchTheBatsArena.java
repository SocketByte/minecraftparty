package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.ArenaInfo;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PunchTheBatsArena extends Arena {
    public PunchTheBatsArena(Game game) {
        super(game);
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(), getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {
        getGame().broadcast("&cArena PunchTheBats will start in few seconds...");
    }

    @Override
    public void onTick(long timeLeft) {
        getGame().broadcast("&cArena PunchTheBats ends in " + timeLeft + " seconds.");
        for (Player player : getGame().getPlaying()) {
            this.addInternalScore(player, 1);
        }
    }

    @Override
    public void onFreeze() {
        for (Player player : getGame().getPlaying()) {
            getGame().addPoints(player, this.getInternalScore(player));
        }
        getGame().broadcast("&cArena PunchTheBats freezed, next arena in few seconds...");
    }

    @Override
    public void onInit() {
        getGame().broadcast("&cArena PunchTheBats initialized!");
        this.getBoard().setType(ArenaBoardType.SCORES);
    }

    @Override
    public void onStart() {
        getGame().broadcast("&cArena PunchTheBats started!");
    }

    @Override
    public void onEnd() {
        getGame().broadcast("&cArena PunchTheBats ended!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        getGame().broadcast("&cBlock break in arena PunchTheBats!");
    }
}
