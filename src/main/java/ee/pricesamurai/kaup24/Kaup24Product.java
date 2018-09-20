package ee.pricesamurai.kaup24;

import ee.pricesamurai.Product;

public class Kaup24Product extends Product {

    private double coupon;

    public Kaup24Product(String id, String name, double price, String url) {
        super(id, name, price, url);
    }

    public double getCoupon() {
        return coupon;
    }

    public void setCoupon(double coupon) {
        this.coupon = coupon;
    }
}
