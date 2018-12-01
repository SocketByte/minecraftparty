package pl.socketbyte.minecraftparty.basic;

import pl.socketbyte.minecraftparty.basic.arena.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ArenaContainer {

    private static final Map<String, Class<? extends Arena>> arenaNames = new HashMap<>();

    static {
        arenaNames.put("punch-the-bats", PunchTheBatsArena.class);
        arenaNames.put("trampolinio", TrampolinioArena.class);
        arenaNames.put("wool-mix-up", WoolMixupArena.class);
        arenaNames.put("chicken-game", ChickenGameArena.class);
        arenaNames.put("horse-race", HorseRaceArena.class);
        arenaNames.put("minefield", MinefieldArena.class);
        arenaNames.put("diamond-mine", DiamondMineArena.class);
        arenaNames.put("spleef", SpleefArena.class);
    }

    public static void addArena(String arenaName, Class<? extends Arena> arenaClass) {
        arenaNames.put(arenaName, arenaClass);
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
