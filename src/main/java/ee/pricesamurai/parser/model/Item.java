package ee.pricesamurai.parser.model;

public class Item {

    private String url;
    private Integer itemId;

    public Item(String url, Integer itemId) {
        this.url = url;
        this.itemId = itemId;
    }

    public String getUrl() {
        return url;
    }

    public Integer getItemId() {
        return itemId;
    }
}
