package ee.pricesamurai.parser.model;

import ee.pricesamurai.database.DatabaseController;

import java.sql.SQLException;

public interface Parser {

    void runParser(DatabaseController databaseController) throws SQLException;
}
