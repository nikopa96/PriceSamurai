package ee.pricesamurai.parser.amazon;

import ee.pricesamurai.database.DatabaseController;
import ee.pricesamurai.parser.DomNotFoundException;
import ee.pricesamurai.parser.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AmazonParser {

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
                nextAmazonUrl = url + "&startIndex=" + String.valueOf(increment);
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

                for (Element element : divElements) {
                    if (element.getElementsByClass("olpSellerName").html().contains("img alt=\"Amazon.de\"")) {
                        String name = document.getElementById("olpProductDetails").getElementsByTag("h1")
                                .get(0)
                                .text();
                        String price = element.getElementsByClass("olpOfferPrice").get(0).text();

                        if (!name.isEmpty() && !price.isEmpty()) {
                            float formattedPrice = Float.parseFloat(price.replace("EUR ", "")
                                    .replace(",", "."));
                            amazonProducts.add(new Product(name, formattedPrice, productUrl));
                        } else {
                            throw new DomNotFoundException("Cannot find URL or DOM element");
                        }
                    }
                }
            } catch (IOException | DomNotFoundException e) {
                e.printStackTrace();
            }
        }

        return amazonProducts;
    }

    public void runParser(DatabaseController databaseController) throws SQLException {
        List<String> amazonUrlList = databaseController.fetchUrlFromDatabase("amazon.de");
        List<Product> amazonProduct = parseAndCreateProducts(amazonUrlList);

        databaseController.addProductsToDatabase(amazonProduct);
    }
}
