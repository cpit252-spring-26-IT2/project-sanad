package sa.edu.kau.fcit.cpit252.project;

import java.util.*;

// This class represents the categories like plumbing supplies and hand tools
// that contain individual tools.
public class ToolCategory implements CatalogComponent {
    // Here we define the category by a name and a list of tools
    private String categoryName;
    public List<CatalogComponent> components = new ArrayList<>();

    public ToolCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    // Here we add the tools to the category
    public void addComponent(CatalogComponent component) {
        components.add(component);
    }

    @Override
    public String getName() {
        return this.categoryName;
    }

    // Here we calculate the total price of all tools in the category
    @Override
    public double getPrice() {
        double total = 0.0;
        for (CatalogComponent component : components) {
            total += component.getPrice();
        }
        return total;
    }

    @Override
    public String getVariant() {
        return "Multiple Options Inside";
    }

    // Here we return all the categories in the category
    @Override
    public Set<String> getAllCategories() {
        Set<String> allCategories = new HashSet<>();

        for (CatalogComponent component : components) {

            allCategories.add(component.getName());
        }

        return allCategories;
    }


    // Here we print the category and all the tools inside it in a tree structure
    @Override
    public String toString() {
        return toString("");
    }

    public String toString(String indent) {
        StringBuilder sb = new StringBuilder();

        sb.append(indent).append("[").append(this.categoryName).append("]\n");

        for (CatalogComponent component : components) {
            if (component instanceof ToolCategory) {
                sb.append(((ToolCategory) component).toString(indent + "  "));
            } else {
                sb.append(indent).append("  ").append(component.toString()).append("\n");
            }
        }

        return sb.toString();
    }
}
