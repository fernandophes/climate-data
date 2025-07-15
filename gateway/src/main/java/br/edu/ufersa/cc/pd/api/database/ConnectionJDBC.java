package br.edu.ufersa.cc.pd.api.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionJDBC.class.getSimpleName());

    private static final String URL = "jdbc:postgresql://localhost:5432/weather";
    private static final String USER = "weather";
    private static final String PASSWORD = "weather";

    private static ConnectionJDBC instance;
    private Connection connection;

    public ConnectionJDBC() throws SQLException {
        this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
        LOG.info("Database Connection Created With success!!");
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static ConnectionJDBC getInstance() throws SQLException {
        if (instance == null) {
            instance = new ConnectionJDBC();
        } else if (instance.getConnection().isClosed()) {
            instance = new ConnectionJDBC();
        }

        return instance;
    }
}
