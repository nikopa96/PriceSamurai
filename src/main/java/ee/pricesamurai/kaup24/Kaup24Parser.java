package ee.pricesamurai.kaup24;

import ee.pricesamurai.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Kaup24Parser {

    private static final String BASE_URL = "https://kaup24.ee";

    private List<String> url;
    private List<Product> kaup24products = new ArrayList<>();

    private void addAllToUrlList() {
        try (Stream<String> stream = Files.lines(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("kaup24/url.txt")).toURI()))) {
            url = stream.collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void parseAndCreateProduct() {
        addAllToUrlList();

        for (String productUrl : url) {
            try {
                Document document = Jsoup.connect(BASE_URL + productUrl).get();
                String name = document.select("#productPage > section:nth-child(3) > div > h1").get(0).text();
                String priceRaw = document
                        .getElementsByAttributeValueContaining("widget-attachpoint", "sellPrice")
                        .get(0).html();
                String price = priceRaw.substring(priceRaw.indexOf("content=\"") + 9, priceRaw.indexOf("\">"));

                Product kaup24product = new Kaup24Product(null, name, Double.parseDouble(price), productUrl);
                kaup24products.add(kaup24product);

                System.out.println("ADDED KAUP24 PRODUCT: " + name + ", " + price);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Product> getKaup24products() {
        parseAndCreateProduct();
        return kaup24products;
    }
}
