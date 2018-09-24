package ee.pricesamurai.database;

import ee.pricesamurai.parser.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private Connection connection;

    public DatabaseController(Connection connection) {
        this.connection = connection;
    }

    public List<String> fetchUrlFromDatabase(String webStore) throws SQLException {
        List<String> webStoreUrlList = new ArrayList<>();

        String sqlRequest = "SELECT * FROM pages WHERE url LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);
        preparedStatement.setString(1, "%" + webStore + "%");

        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String url = resultSet.getString("url");
            webStoreUrlList.add(url);
        }
        preparedStatement.close();

        return webStoreUrlList;
    }

    public void addProductsToDatabase(List<Product> products) {
        String sqlRequest = "INSERT INTO product(name, price, url) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);

            for (Product product : products) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setFloat(2, product.getPrice());
                preparedStatement.setString(3, product.getUrl());
                preparedStatement.execute();
            }

            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
