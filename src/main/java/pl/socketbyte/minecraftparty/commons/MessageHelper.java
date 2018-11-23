package pl.socketbyte.minecraftparty.commons;

import io.github.theluca98.textapi.ActionBar;
import io.github.theluca98.textapi.Title;
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

    public static Title sendTitle(Player player, String header, String sub, int fadeIn, int stay, int fadeOut) {
        Title title = new Title(
                MessageHelper.fixColor(header),
                MessageHelper.fixColor(sub),
                fadeIn, stay, fadeOut);
        title.send(player);
        return title;
    }

    public static ActionBar sendActionBar(Player player, String message) {
        ActionBar bar = new ActionBar(
                MessageHelper.fixColor(message));
        bar.send(player);
        return bar;
    }

    public static String capitalize(String name) {
        String[] s = name.trim().toLowerCase().split("\\s+");
        StringBuilder nameBuilder = new StringBuilder();
        for (String i : s){
            if(i.equals("")) return nameBuilder.toString(); // or return anything you want
            nameBuilder.append(i.substring(0, 1).toUpperCase()).append(i.substring(1)).append(" "); // uppercase first char in words
        }
        name = nameBuilder.toString();
        return name.trim();
    }
}
