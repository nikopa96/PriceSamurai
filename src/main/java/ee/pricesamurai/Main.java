package ee.pricesamurai;

import ee.pricesamurai.database.DBConnection;
import ee.pricesamurai.parser.kaup24.Kaup24Parser;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        DBConnection dbConnection = new DBConnection();
        Kaup24Parser kaup24Parser = new Kaup24Parser();
        kaup24Parser.getKaup24products(dbConnection.getConnection());
        dbConnection.getConnection().close();
    }
}
