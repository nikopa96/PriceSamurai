package ee.pricesamurai.parser.oneA;

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

public class OneAParser implements Parser {

    private int errorsCounter;

    public OneAParser(int errorsCounter) {
        this.errorsCounter = errorsCounter;
    }

    private float formatPrice(Elements priceRaw) {
        String euros = priceRaw.get(0).html()
                .replaceAll("<sub>\\u20AC</sub>", "")
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
            } catch (IOException | DomNotFoundException | NumberFormatException e) {
                this.errorsCounter++;
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }

        return oneAProducts;
    }

    public int getErrorsCounter() {
        return errorsCounter;
    }

    @Override
    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> oneAUrlList = databaseController.fetchUrlFromDatabase("1a.ee");
        List<Product> oneAProducts = parseAndCreateProducts(oneAUrlList);

        databaseController.addProductsToDatabase(oneAProducts);
    }
}
