package ee.pricesamurai.parser.kaup24;

import ee.pricesamurai.parser.DomNotFoundException;
import ee.pricesamurai.parser.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Kaup24Parser {

    private List<String> urlList = new ArrayList<>();
    private List<Product> kaup24products = new ArrayList<>();

    private void fetchUrlFromDatabase(Connection connection) throws SQLException {
        String sqlRequest = "SELECT * FROM pages WHERE url LIKE '%kaup24.ee%'";
        PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String url = resultSet.getString("url");
            urlList.add(url);
        }
        preparedStatement.close();
    }

    private void parseAndCreateProduct() {
        for (String productUrl : urlList) {
            try {
                Document document = Jsoup.connect(productUrl).get();
                String name = document.select("#productPage > section:nth-child(3) > div > h1").get(0).text();
                String price = document.getElementsByAttributeValue("itemprop", "price")
                        .get(0)
                        .attr("content");

                if (!name.isEmpty() && !price.isEmpty()) {
                    Product kaup24product = new Kaup24Product(name, Float.parseFloat(price), productUrl);
                    kaup24products.add(kaup24product);

                    System.out.println("Parsed Kaup24 product: " + name + ", price: " + price);
                } else {
                    throw new DomNotFoundException("Cannot find URL or DOM element");
                }
            } catch (IOException | DomNotFoundException e) {
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }
    }

    private void addProductsToDatabase(Connection connection) {
        String sqlRequest = "INSERT INTO product(name, price, url) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);

            for (Product product : kaup24products) {
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

    public List<Product> getKaup24products(Connection connection) throws SQLException {
        fetchUrlFromDatabase(connection);
        parseAndCreateProduct();
        addProductsToDatabase(connection);
        return kaup24products;
    }
}
