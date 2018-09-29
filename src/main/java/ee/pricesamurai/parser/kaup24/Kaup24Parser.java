package ee.pricesamurai.parser.kaup24;

import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.model.DomNotFoundException;
import ee.pricesamurai.parser.model.Parser;
import ee.pricesamurai.parser.model.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Kaup24Parser implements Parser {

    private int errorsCounter;

    public Kaup24Parser(int errorsCounter) {
        this.errorsCounter = errorsCounter;
    }

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
                    Elements elements = document.getElementsByAttributeValue("widget-attachpoint", "globalBadgeTitle");

                    Product kaup24product = new Kaup24Product(name, Float.parseFloat(price), productUrl);
                    kaup24products.add(kaup24product);
                } else {
                    throw new DomNotFoundException("Cannot find URL or DOM element");
                }
            } catch (IOException | DomNotFoundException| NumberFormatException e) {
                this.errorsCounter++;
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }

        return kaup24products;
    }

    public int getErrorsCounter() {
        return errorsCounter;
    }

    @Override
    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> kaup24UrlList = databaseController.fetchUrlFromDatabase("kaup24.ee");
        List<Product> kaup24Products = parseAndCreateProducts(kaup24UrlList);

        databaseController.addProductsToDatabase(kaup24Products);
    }
}
