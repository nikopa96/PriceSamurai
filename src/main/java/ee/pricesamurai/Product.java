package ee.pricesamurai;

public class Product {

    private String id;
    private String name;
    private double price;
    private String url;

    public Product(String id, String name, double price, String url) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getUrl() {
        return url;
    }
}
