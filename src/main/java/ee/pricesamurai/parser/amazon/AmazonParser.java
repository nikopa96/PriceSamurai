package ee.pricesamurai.parser.amazon;

import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.model.DomNotFoundException;
import ee.pricesamurai.parser.model.Parser;
import ee.pricesamurai.parser.model.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AmazonParser implements Parser {

    private int errorsCounter;

    public AmazonParser(int errorsCounter) {
        this.errorsCounter = errorsCounter;
    }

    private Document getHtmlContainingAmazonOffer(String url) throws IOException {
        Document document = null;
        String nextAmazonUrl = url;
        int increment = 0;

        for (int i = 0; i < 10; i++) {
            document = Jsoup.connect(nextAmazonUrl).get();

            if (document.getElementById("olpOfferListColumn").html().contains("img alt=\"Amazon.de\"")) {
                return document;
            } else {
                increment = increment + 10;
                nextAmazonUrl = url + "/ref=olp_f_new?ie=UTF8&f_new=true&startIndex=" + String.valueOf(increment);
            }
        }

        return document;
    }

    private List<Product> parseAndCreateProducts(List<String> amazonUrlList) {
        List<Product> amazonProducts = new ArrayList<>();

        for (String productUrl : amazonUrlList) {
            try {
                Document document = getHtmlContainingAmazonOffer(productUrl);
                Elements divElements = document.getElementsByClass("olpOffer");

                String name = document.getElementById("olpProductDetails").getElementsByTag("h1")
                        .get(0)
                        .text();
                String price = "";

                for (Element element : divElements) {
                    if (element.getElementsByClass("olpSellerName").html().contains("img alt=\"Amazon.de\"")) {
                        price = element.getElementsByClass("olpOfferPrice").get(0).text();

                        float formattedPrice = Float.parseFloat(price.replace("EUR ", "")
                                .replace(",", "."));
                        amazonProducts.add(new Product(name, formattedPrice, productUrl));
                    }
                }

                if (name.isEmpty() || price.isEmpty()) {
                    throw new DomNotFoundException("Cannot find DOM element");
                }
            } catch (IOException | DomNotFoundException e) {
                this.errorsCounter++;
                System.out.println(e.getMessage() + " REQUEST: " + productUrl);
            }
        }

        return amazonProducts;
    }

    public int getErrorsCounter() {
        return errorsCounter;
    }

    @Override
    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> amazonUrlList = databaseController.fetchUrlFromDatabase("amazon.de");
        List<Product> amazonProduct = parseAndCreateProducts(amazonUrlList);

        databaseController.addProductsToDatabase(amazonProduct);
    }
}
