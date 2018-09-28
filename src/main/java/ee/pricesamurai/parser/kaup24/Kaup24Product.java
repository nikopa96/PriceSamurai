package ee.pricesamurai.parser.kaup24;

import ee.pricesamurai.parser.model.Product;

public class Kaup24Product extends Product {

    private float couponDiscount;
    private float couponMinSum;

    public Kaup24Product(String name, float price, String url) {
        super(name, price, url);
    }

    public float getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(float couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public float getCouponMinSum() {
        return couponMinSum;
    }

    public void setCouponMinSum(float couponMinSum) {
        this.couponMinSum = couponMinSum;
    }
}
