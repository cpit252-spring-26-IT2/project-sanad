package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class CategoryFilter extends ProductFilterHandler {
    @Override
    protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
        if (criteria == null || isBlank(criteria.getCategory())) {
            return new ArrayList<>(products);
        }

        String wantedCategory = criteria.getCategory().trim();
        List<ProductListing> filteredProducts = new ArrayList<>();

        for (ProductListing product : products) {
            if (product == null) {
                continue;
            }

            if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(wantedCategory)) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
