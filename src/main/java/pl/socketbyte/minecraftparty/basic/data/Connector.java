package pl.socketbyte.minecraftparty.basic.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class Connector {

    private HikariDataSource dataSource;
    private HikariConfig config;

    public Connector(HikariConfig config) {
        this.config = config;
    }

    public Connector(String host, String user, String password, String database, int port) {
        this.config = new HikariConfig();
        this.config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        this.config.setUsername(user);
        this.config.setPassword(password);
        this.config.addDataSourceProperty("cachePrepStmts", "true");
        this.config.addDataSourceProperty("prepStmtCacheSize", 250);
        this.config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);

        this.config.setLeakDetectionThreshold(10000); // TODO remove this after deploying to production
    }

    public void init() {
        this.dataSource = new HikariDataSource(this.config);
    }

    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
