package pl.socketbyte.minecraftparty.commons;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageHelper {

    private MessageHelper() {
    }

    public static String fixColor(String unformattedText) {
        return ChatColor.translateAlternateColorCodes('&', unformattedText);
    }

    public static void send(Player player, String message) {
        player.sendMessage(fixColor(message));
    }

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(fixColor(message));
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(fixColor(message));
    }

}
