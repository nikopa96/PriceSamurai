package ee.pricesamurai.parser.oneA;

import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.DomNotFoundException;
import ee.pricesamurai.parser.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OneAParser {

    private float formatPrice(Elements priceRaw) {
        String euros = priceRaw.get(0).html()
                .replace("<sub>€</sub>", "")
                .replaceAll("<sup>\\d+</sup>", "")
                .replaceAll("\\n", "");

        String cents = priceRaw.get(0).getElementsByTag("sup").get(0).text();

        return Float.parseFloat(euros + "." + cents);
    }

    private List<Product> parseAndCreateProducts(List<String> oneAUrlList) {
        List<Product> oneAProducts = new ArrayList<>();

        for (String productUrl : oneAUrlList) {
            try {
                Document document = Jsoup.connect(productUrl).get();
                String name = document.getElementsByClass("product-main-info").get(0)
                        .getElementsByTag("h1").get(0).text();
                Elements priceRaw = document.getElementsByClass("price-holder").get(0)
                        .getElementsByClass("price");

                if (!name.isEmpty() && !priceRaw.isEmpty()) {
                    float formattedPrice = formatPrice(priceRaw);
                    oneAProducts.add(new Product(name, formattedPrice, productUrl));
                } else {
                    throw new DomNotFoundException("Cannot find DOM element");
                }
            } catch (IOException | DomNotFoundException e) {
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }

        return oneAProducts;
    }

    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> oneAUrlList = databaseController.fetchUrlFromDatabase("1a.ee");
        List<Product> oneAProducts = parseAndCreateProducts(oneAUrlList);

        databaseController.addProductsToDatabase(oneAProducts);
    }
}
