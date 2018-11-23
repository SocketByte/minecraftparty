package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.basic.board.impl.ArenaBoardType;
import pl.socketbyte.minecraftparty.commons.RandomHelper;
import pl.socketbyte.minecraftparty.commons.TaskHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrampolinioArena extends Arena {

    public TrampolinioArena(Game game) {
        super(game);
    }

    @Override
    public List<Location> getPlayerStartPositions() {
        return RandomHelper.createLocations(getArenaInfo().getDefaultLocation(),
                getGame().getPlaying().size());
    }

    @Override
    public void onCountdown() {
        getGame().broadcast("&aArena Trampolinio will start in few seconds...");
    }

    @Override
    public void onTick(long timeLeft) {
        getGame().broadcast("&aArena Trampolinio ends in " + timeLeft + " seconds.");
    }

    @Override
    public void onFreeze() {
        for (Player player : getGame().getPlaying()) {
            getGame().addPoints(player, 1000);
        }
        getGame().broadcast("&aArena Trampolinio freezed.");
    }

    @Override
    public void onInit() {
        getGame().broadcast("&aArena Trampolinio initialized!");
        this.getBoard().setType(ArenaBoardType.DISQUALIFICATION);
    }

    @Override
    public void onStart() {
        getGame().broadcast("&aArena Trampolinio started!");
    }

    @Override
    public void onEnd() {
        getGame().broadcast("&aArena Trampolinio ended!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        getGame().broadcast("&aBlock break in arena Trampolinio!");
    }
}
