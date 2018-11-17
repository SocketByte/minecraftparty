package pl.socketbyte.minecraftparty.basic.arena;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import pl.socketbyte.minecraftparty.basic.Arena;
import pl.socketbyte.minecraftparty.basic.Game;
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
    public void onInit() {
        getGame().broadcast("Arena Trampolinio initialized!");
    }

    @Override
    public void onStart() {
        getGame().broadcast("Arena Trampolinio started!");
        getTaskManager().schedule(() -> {
            if (getArenaInfo().getTimeLeftAsSeconds() <= 0) {
                end();
                return;
            }

            getGame().broadcast("Arena Trampolinio ends in " + getArenaInfo().getTimeLeftAsSeconds() + " seconds.");
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void onEnd() {
        getGame().broadcast("Arena Trampolinio ended!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().isPlaying(event.getPlayer()))
            return;

        if (!getGame().isArena(this))
            return;

        getGame().broadcast("Block break in arena Trampolinio!");
    }
}
