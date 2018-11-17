package pl.socketbyte.minecraftparty.command.impl;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.MinecraftParty;
import pl.socketbyte.minecraftparty.basic.Game;
import pl.socketbyte.minecraftparty.command.Command;
import pl.socketbyte.minecraftparty.command.CommandInterface;

@Command(name = "join", permission = "join", usage = "/join [arena]", aliases = {"j"})
public class JoinGameCommand extends CommandInterface {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        String arena = args[0];
        Player player = (Player)sender;

        Game.getController().join(player, arena);
    }
}
