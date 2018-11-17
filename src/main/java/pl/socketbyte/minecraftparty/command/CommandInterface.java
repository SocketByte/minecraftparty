package pl.socketbyte.minecraftparty.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.commons.MessageHelper;
import pl.socketbyte.minecraftparty.commons.io.I18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CommandInterface extends org.bukkit.command.Command {

    private final boolean onlyPlayer;

    protected CommandInterface() {
        super("");

        Command command = getClass().getAnnotation(Command.class);
        setName(command.name());
        setPermission(command.permission().equalsIgnoreCase("none")
                ? "minecraftparty.command." + command.name()
                : command.permission());
        setUsage(command.usage());
        setAliases(Arrays.asList(command.aliases()));

        this.onlyPlayer = command.onlyPlayer();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(getPermission())) {
            MessageHelper.send(sender, I18n.get().message("no_permission", "%permission%", getPermission()));
            return true;
        }
        if (this.onlyPlayer && !(sender instanceof Player)) {
            MessageHelper.send(sender, I18n.get().message("only_player"));
            return true;
        }
        try {
            onCommand(sender, args);
        } catch (Exception e) {
            MessageHelper.send(sender, "&cMinecraftParty napotkalo powazny blad. Skontaktuj sie z administracja!");
            System.out.println("An error occured when executing command /" + getName() + ". Args: " + Arrays.toString(args));
            e.printStackTrace();
        }
        return true;
    }

    public abstract void onCommand(CommandSender sender, String[] args);

    // because fuck tab completion on pre 1.12
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return new ArrayList<>();
    }
}
