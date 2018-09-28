package ee.pricesamurai;

import ee.pricesamurai.database.DBConnection;
import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.amazon.AmazonParser;
import ee.pricesamurai.parser.kaup24.Kaup24Parser;
import ee.pricesamurai.parser.oneA.OneAParser;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    private static String getCurrentTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        return dateFormat.format(date);
    }

    public static void main(String[] args) throws SQLException {

        System.out.println("------------------------  Begin:  " + getCurrentTimestamp() + "  ------------------------");

        DBConnection dbConnection = new DBConnection();
        DatabaseController databaseController = new DatabaseController(dbConnection.getConnection());

        int errorsCounter = 0;

        Kaup24Parser kaup24Parser = new Kaup24Parser(errorsCounter);
        kaup24Parser.runParser(databaseController);

        AmazonParser amazonParser = new AmazonParser(errorsCounter);
        amazonParser.runParser(databaseController);

        OneAParser oneAParser = new OneAParser(errorsCounter);
        oneAParser.runParser(databaseController);

        dbConnection.getConnection().close();

        errorsCounter = kaup24Parser.getErrorsCounter() + amazonParser.getErrorsCounter() + oneAParser.getErrorsCounter();
        System.out.println("Errors: " + errorsCounter);
        System.out.println("--------------------------  End:  " + getCurrentTimestamp() + "  ------------------------");
        System.out.println("\n");
    }
}
