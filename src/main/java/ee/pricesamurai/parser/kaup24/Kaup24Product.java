package ee.pricesamurai.parser.kaup24;

import ee.pricesamurai.parser.Product;

public class Kaup24Product extends Product {

    private double coupon;

    public Kaup24Product(String name, float price, String url) {
        super(name, price, url);
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }
}
