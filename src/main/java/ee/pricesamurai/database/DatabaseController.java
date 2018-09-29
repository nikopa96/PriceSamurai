package ee.pricesamurai.database;

import ee.pricesamurai.parser.kaup24.Kaup24Product;
import ee.pricesamurai.parser.model.Product;

import java.sql.*;
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

    private void addKaup24ProductToDatabase(PreparedStatement preparedStatement,
                                            PreparedStatement statementForKaup24, Product product) throws SQLException {

        if (product instanceof Kaup24Product) {
            Kaup24Product kaup24Product = (Kaup24Product) product;

            if (kaup24Product.getCouponDiscount() != 0 && kaup24Product.getCouponMinSum() != 0) {
                ResultSet newResultSet = preparedStatement.getGeneratedKeys();

                if (newResultSet.next()) {
                    statementForKaup24.setInt(1, newResultSet.getInt(1));
                    statementForKaup24.setFloat(2, kaup24Product.getCouponDiscount());
                    statementForKaup24.setFloat(3, kaup24Product.getCouponMinSum());
                    statementForKaup24.execute();
                }
            }
        }
    }

    public void addProductsToDatabase(List<Product> products) {
        String productSqlRequest = "INSERT INTO product(name, price, url) VALUES(?, ?, ?)";
        String kaup24SqlRequest = "INSERT INTO kaup24(product_id, coupon_discount, coupon_min_sum) VALUES(?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(productSqlRequest, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement statementForKaup24 = connection.prepareStatement(kaup24SqlRequest);

            for (Product product : products) {
                preparedStatement.setString(1, product.getName());
                preparedStatement.setFloat(2, product.getPrice());
                preparedStatement.setString(3, product.getUrl());
                preparedStatement.execute();

                addKaup24ProductToDatabase(preparedStatement, statementForKaup24, product);
            }

            preparedStatement.close();
            statementForKaup24.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
