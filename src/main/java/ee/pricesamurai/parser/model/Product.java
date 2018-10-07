package ee.pricesamurai.parser.model;

public class Product {

    private String name;
    private float price;
    private String url;
    private Integer itemId;

    public Product(String name, float price, String url, Integer itemId) {
        this.name = name;
        this.price = price;
        this.url = url;
        this.itemId = itemId;
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

    public Integer getItemId() {
        return itemId;
    }
}
