package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public class KeywordFilter extends ProductFilterHandler {
    @Override
    protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
        if (criteria == null || isBlank(criteria.getKeyword())) {
            return new ArrayList<>(products);
        }

        String keyword = criteria.getKeyword().trim().toLowerCase();
        List<ProductListing> filteredProducts = new ArrayList<>();

        for (ProductListing product : products) {
            if (product == null) {
                continue;
            }

            boolean matchesName = containsIgnoreCase(product.getProductName(), keyword);
            boolean matchesVariant = containsIgnoreCase(product.getVariant(), keyword);
            boolean matchesCategory = containsIgnoreCase(product.getCategory(), keyword);
            boolean matchesShop = containsIgnoreCase(product.getShopName(), keyword);

            if (matchesName || matchesVariant || matchesCategory || matchesShop) {
                filteredProducts.add(product);
            }
        }

        return filteredProducts;
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        if (value == null) {
            return false;
        }
        return value.toLowerCase().contains(keyword);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
