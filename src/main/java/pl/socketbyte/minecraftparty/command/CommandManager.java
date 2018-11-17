package pl.socketbyte.minecraftparty.command;

import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import pl.socketbyte.minecraftparty.MinecraftParty;

import java.lang.reflect.Field;

public class CommandManager {

    private final String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
    private SimpleCommandMap commandMap;

    public CommandManager() {
        setup();
    }

    private void setup() {
        try {
            Class craftServerClass = Class.forName("org.bukkit.craftbukkit." + bukkitVersion + ".CraftServer");
            Field f = craftServerClass.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (SimpleCommandMap) f.get(craftServerClass.cast(MinecraftParty.getInstance().getServer()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerCommand(org.bukkit.command.Command command) {
        commandMap.register("crazycore", command);
    }

    public void registerCommands(org.bukkit.command.Command... commands) {
        for (org.bukkit.command.Command command : commands)
            commandMap.register(command.getName(), command);
    }

}
