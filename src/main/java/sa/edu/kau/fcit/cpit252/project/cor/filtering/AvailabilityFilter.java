package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class AvailabilityFilter extends ProductFilterHandler {
    @Override
    protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
        if (criteria == null || criteria.getAvailableOnly() == null || !criteria.getAvailableOnly()) {
            return new ArrayList<>(products);
        }

        List<ProductListing> filteredProducts = new ArrayList<>();

        for (ProductListing product : products) {
            if (product == null) {
                continue;
            }

            if (product.isAvailable()) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }
}
