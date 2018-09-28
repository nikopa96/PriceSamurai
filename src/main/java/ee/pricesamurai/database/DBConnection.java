package ee.pricesamurai.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_PATH = "jdbc:postgresql://localhost:5432/samuraiParser";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";

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
