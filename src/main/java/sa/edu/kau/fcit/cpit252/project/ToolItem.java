package sa.edu.kau.fcit.cpit252.project;

import java.util.HashSet;
import java.util.Set;

// This class is a concrete class that represents individual tools.
public class ToolItem implements CatalogComponent {

    // Here we define the tool by a name, variant (the different types of each tool), and price of the tool.
    private String name;
    private String variant;
    private double price;

    public ToolItem(String name, String variant, double price) {
        this.name = name;
        this.variant = variant;
        this.price = price;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public double getPrice() {
        return this.price;
    }

    @Override
    public String getVariant() {
        return this.variant;
    }

    @Override
    // Products return an empty set because we don't want
    // specific product names to clutter the category filter list.
    public Set<String> getAllCategories() {
        return new HashSet<>();
    }

    // Here we return string for the individual tool
    @Override
    public String toString() {
        return "  - " + this.name + " (" + this.variant + ") : SAR " + this.price;
    }
}
