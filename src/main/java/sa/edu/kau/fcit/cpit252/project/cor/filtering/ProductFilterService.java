package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class ProductFilterService {
    public List<ProductListing> filterProducts(List<ProductListing> products, FilterCriteria criteria) {
        if (products == null) {
            return new ArrayList<>();
        }

        if (criteria == null) {
            return new ArrayList<>(products);
        }

        ProductFilterHandler firstFilter = buildFilterChain();
        return firstFilter.handle(products, criteria);
    }

    private ProductFilterHandler buildFilterChain() {
        ProductFilterHandler keywordFilter = new KeywordFilter();
        ProductFilterHandler categoryFilter = new CategoryFilter();
        ProductFilterHandler priceRangeFilter = new PriceRangeFilter();
        ProductFilterHandler availabilityFilter = new AvailabilityFilter();
        ProductFilterHandler ratingFilter = new RatingFilter();

        keywordFilter.setNextHandler(categoryFilter)
                .setNextHandler(priceRangeFilter)
                .setNextHandler(availabilityFilter)
                .setNextHandler(ratingFilter);

        return keywordFilter;
    }
}
