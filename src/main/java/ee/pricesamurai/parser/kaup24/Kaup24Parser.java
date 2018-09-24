package ee.pricesamurai.parser.kaup24;

import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.DomNotFoundException;
import ee.pricesamurai.parser.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Kaup24Parser {

    private List<Product> parseAndCreateProducts(List<String> kaup24UrlList) {
        List<Product> kaup24products = new ArrayList<>();

        for (String productUrl : kaup24UrlList) {
            try {
                Document document = Jsoup.connect(productUrl).get();
                String name = document.select("#productPage > section:nth-child(3) > div > h1").get(0).text();
                String price = document.getElementsByAttributeValue("itemprop", "price")
                        .get(0)
                        .attr("content");

                if (!name.isEmpty() && !price.isEmpty()) {
                    Product kaup24product = new Product(name, Float.parseFloat(price), productUrl);
                    kaup24products.add(kaup24product);

                    System.out.println("Parsed Kaup24 product: " + name + ", price: " + price);
                } else {
                    throw new DomNotFoundException("Cannot find URL or DOM element");
                }
            } catch (IOException | DomNotFoundException e) {
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }

        return kaup24products;
    }

    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> kaup24UrlList = databaseController.fetchUrlFromDatabase("kaup24.ee");
        List<Product> kaup24Products = parseAndCreateProducts(kaup24UrlList);

        databaseController.addProductsToDatabase(kaup24Products);
    }
}
