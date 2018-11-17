package pl.socketbyte.minecraftparty.basic.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Model interface
 *
 * Warning: Do not close the connection passed as an argument. It is AutoCloseable.
 */
public interface Model {
    PreparedStatement update(Connection connection) throws SQLException;
    PreparedStatement delete(Connection connection) throws SQLException;
    PreparedStatement insert(Connection connection) throws SQLException;
}
