package pl.socketbyte.minecraftparty.basic;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.arena.PunchTheBatsArena;

import java.util.HashMap;
import java.util.Map;

public class GameController {

    private final Map<String, Game> games = new HashMap<>();

    public void add(Game game) {
        this.games.put(game.getName(), game);
    }

    public void remove(Game game) {
        this.games.remove(game.getName());
    }

    public Game get(String id) {
        return games.get(id);
    }

    public void dispose() {
        for (Game game : games.values()) {
            game.dispose();
        }
    }

    public GameController() {
    }

    public void loadGameInfos() {
        ConfigurationSection section = MinecraftParty.getInstance().getConfig()
                .getConfigurationSection("games");
        for (String key : section.getKeys(false)) {
            new GameInfo(section.getConfigurationSection(key));

            Game game = create(key);
            System.out.println("Loaded " + game.getName() + ". GameInfo: " + game.getGameInfo());
        }
    }

    public Game create(String id) {
        Game game = new Game(id, 16);
        for (ArenaInfo info : game.getGameInfo().getArenaInfos()) {
            Arena arena = ArenaContainer.createArenaInstance(game, info.getName());

            game.addArena(arena);
        }
        add(game);
        System.out.println("Created game with id " + id);
        return game;
    }

    public void join(Player player, String id) {
        Game game = get(id);

        game.join(player);
    }

    public void leave(Player player, String id) {
        Game game = get(id);

        game.leave(player, false);
    }
}
