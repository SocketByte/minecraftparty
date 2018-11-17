package pl.socketbyte.minecraftparty.basic;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.socketbyte.minecraftparty.basic.data.Connector;
import pl.socketbyte.minecraftparty.basic.data.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class User implements Model {

    private final UUID uniqueId;
    private final String name;

    public User(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public User(Player player) {
        this.uniqueId = player.getUniqueId();
        this.name = player.getName();
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.uniqueId);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uniqueId);
    }

    public boolean isOnline() {
        return getOfflinePlayer().isOnline();
    }

    @Override
    public PreparedStatement update(Connection connection) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement delete(Connection connection) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement insert(Connection connection) throws SQLException {
        return null;
    }
}
