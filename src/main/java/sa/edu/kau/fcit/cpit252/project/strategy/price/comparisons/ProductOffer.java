package sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons;

public class ProductOffer {
    private final String productName;
    private final String variant;
    private final String shopName;
    private final double price;

    public ProductOffer(String productName, String variant, String shopName, double price) {
        this.productName = productName;
        this.variant = variant;
        this.shopName = shopName;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getVariant() {
        return variant;
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product: " + productName + ", Variant: " + variant + ", Shop: " + shopName
                + ", Price: SAR " + price;
    }
}