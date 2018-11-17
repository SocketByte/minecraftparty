package pl.socketbyte.minecraftparty.basic.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModelPersistence {

    private final Connector connector;

    public ModelPersistence(Connector connector) {
        this.connector = connector;
    }

    public <T extends Model> List<T> selectAll(String table, Class<T> clazz, ModelOperation<T> modelOperation) {
        List<T> results = new ArrayList<>();

        try {
            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM " + table);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        results.add(modelOperation.run(rs));
                    }
                }
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    public void update(Model model) {
        try {
            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = model.update(connection);

                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Model model) {
        try {
            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = model.delete(connection);

                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(Model model) {
        try {
            try (Connection connection = connector.getConnection()) {
                PreparedStatement statement = model.insert(connection);

                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
