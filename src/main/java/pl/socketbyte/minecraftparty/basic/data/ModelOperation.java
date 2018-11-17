package pl.socketbyte.minecraftparty.basic.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ModelOperation<T> {
    T run(ResultSet resultSet) throws SQLException;
}
