package pl.socketbyte.minecraftparty.basic;

import pl.socketbyte.minecraftparty.basic.arena.PunchTheBatsArena;
import pl.socketbyte.minecraftparty.basic.arena.TrampolinioArena;
import pl.socketbyte.minecraftparty.basic.arena.WoolMixupArena;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ArenaContainer {

    private static final Map<String, Class<? extends Arena>> arenaNames = new HashMap<>();

    static {
        arenaNames.put("punch-the-bats", PunchTheBatsArena.class);
        arenaNames.put("trampolinio", TrampolinioArena.class);
        arenaNames.put("wool-mix-up", WoolMixupArena.class);
    }

    public static Arena createArenaInstance(Game game, String arenaName) {
        Class<? extends Arena> clazz = arenaNames.get(arenaName);

        Arena arena = null;
        try {
            arena = clazz.getDeclaredConstructor(Game.class).newInstance(game);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return arena;
    }
}
