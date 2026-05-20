package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class PriceRangeFilter extends ProductFilterHandler {
    @Override
    protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
        if (criteria == null || (criteria.getMinPrice() == null && criteria.getMaxPrice() == null)) {
            return new ArrayList<>(products);
        }

        Double minPrice = criteria.getMinPrice();
        Double maxPrice = criteria.getMaxPrice();
        List<ProductListing> filteredProducts = new ArrayList<>();

        for (ProductListing product : products) {
            if (product == null) {
                continue;
            }

            double price = product.getPrice();
            boolean passesMin = minPrice == null || price >= minPrice;
            boolean passesMax = maxPrice == null || price <= maxPrice;

            if (passesMin && passesMax) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }
}
