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
import java.util.Arrays;
import java.util.List;

public class Kaup24Parser implements Parser {

    private int errorsCounter;

    public Kaup24Parser(int errorsCounter) {
        this.errorsCounter = errorsCounter;
    }

    private List<Float> parseCoupon(Elements elements, String productUrl) {
        List<Float> coupons = new ArrayList<>();

        try {
            String rawCoupon = elements.get(0).getElementsMatchingText("soodustus alates").get(0).text();
            List<String> rawCouponElements = Arrays.asList(rawCoupon.split(" "));

            float couponDiscount = Float.parseFloat(rawCouponElements.get(0).replaceAll("\\u20AC", ""));
            float couponMinSum = Float.parseFloat(rawCouponElements.get(3).replaceAll("\\u20AC", ""));
            coupons = Arrays.asList(couponDiscount, couponMinSum);
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            this.errorsCounter++;
            System.out.println(e.getMessage() + " No coupon: " + productUrl);
        }

        return coupons;
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
                    List<Float> coupons = parseCoupon(elements, productUrl);

                    Kaup24Product kaup24product = new Kaup24Product(name, Float.parseFloat(price), productUrl);

                    if (!coupons.isEmpty()) {
                        kaup24product.setCouponDiscount(coupons.get(0));
                        kaup24product.setCouponMinSum(coupons.get(1));
                    }

                    kaup24products.add(kaup24product);
                } else {
                    throw new DomNotFoundException("Cannot find URL or DOM element");
                }
            } catch (IOException | DomNotFoundException | NumberFormatException e) {
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
