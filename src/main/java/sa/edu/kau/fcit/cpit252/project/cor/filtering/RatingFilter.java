package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class RatingFilter extends ProductFilterHandler {
    @Override
    protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
        if (criteria == null || criteria.getMinRating() == null) {
            return new ArrayList<>(products);
        }

        double minRating = criteria.getMinRating();
        List<ProductListing> filteredProducts = new ArrayList<>();

        for (ProductListing product : products) {
            if (product == null) {
                continue;
            }

            if (product.getRating() >= minRating) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }
}
