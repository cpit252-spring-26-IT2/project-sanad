package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import java.util.ArrayList;
import java.util.List;

public abstract class ProductFilterHandler {
    private ProductFilterHandler nextHandler;

    public ProductFilterHandler setNextHandler(ProductFilterHandler nextHandler) {
        this.nextHandler = nextHandler;
        return nextHandler;
    }

    public List<ProductListing> handle(List<ProductListing> products, FilterCriteria criteria) {
        List<ProductListing> safeProducts;

        if (products == null) {
            safeProducts = new ArrayList<>();
        } else {
            safeProducts = products;
        }

        List<ProductListing> filteredProducts = applyFilter(safeProducts, criteria);

        if (nextHandler == null) {
            return filteredProducts;
        }

        return nextHandler.handle(filteredProducts, criteria);
    }

    protected abstract List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria);
}
