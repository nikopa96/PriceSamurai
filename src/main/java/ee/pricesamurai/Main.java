package ee.pricesamurai;

import ee.pricesamurai.database.DBConnection;
import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.amazon.AmazonParser;
import ee.pricesamurai.parser.kaup24.Kaup24Parser;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {
        DBConnection dbConnection = new DBConnection();
        DatabaseController databaseController = new DatabaseController(dbConnection.getConnection());

//        Kaup24Parser kaup24Parser = new Kaup24Parser();
//        kaup24Parser.runParser(databaseController);

        AmazonParser amazonParser = new AmazonParser();
        amazonParser.runParser(databaseController);

        dbConnection.getConnection().close();
    }
}
