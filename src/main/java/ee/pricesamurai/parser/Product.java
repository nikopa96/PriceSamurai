package ee.pricesamurai.parser;

public class Product {

    private String name;
    private float price;
    private String url;

    public Product(String name, float price, String url) {
        this.name = name;
        this.price = price;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }
}
