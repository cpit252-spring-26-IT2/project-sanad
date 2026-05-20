package sa.edu.kau.fcit.cpit252.project.cor.filtering;

public class ProductListing {
    private final String productName;
    private final String category;
    private final String variant;
    private final String shopName;
    private final double price;
    private final boolean available;
    private final double rating;

    public ProductListing(String productName, String category, String variant, String shopName, double price, boolean available, double rating) {
        this.productName = productName;
        this.category = category;
        this.variant = variant;
        this.shopName = shopName;
        this.price = price;
        this.available = available;
        this.rating = rating;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
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

    public boolean isAvailable() {
        return available;
    }

    public double getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Product: " + productName
                + ", Category: " + category
                + ", Variant: " + variant
                + ", Shop: " + shopName
                + ", Price: SAR " + price
                + ", Available: " + available
                + ", Rating: " + rating;
    }
}
