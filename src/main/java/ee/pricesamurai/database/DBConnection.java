package ee.pricesamurai.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_PATH = "jdbc:postgresql://104.248.132.55:5432/pricesamurai";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "pricesamurai28092018";

    private Connection connection;

    public DBConnection() {
        try {
            connection = DriverManager.getConnection(DB_PATH, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("cannot connect to database");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
