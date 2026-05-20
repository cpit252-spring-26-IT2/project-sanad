package sa.edu.kau.fcit.cpit252.project.cor.filtering;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilteringInternalsUnitTest {

    @Test
    void individualFiltersCoverNullAndPassThroughBranches() {
        ProductListing valid = new ProductListing("Copper Pipe", "Plumbing", "3/4", "Shop A", 20.0, true, 4.5);
        ProductListing unavailable = new ProductListing("Copper Pipe", "Plumbing", "3/4", "Shop B", 30.0, false, 3.0);
        ProductListing missingFields = new ProductListing(null, null, null, null, 15.0, true, 5.0);
        List<ProductListing> products = new ArrayList<>();
        products.add(valid);
        products.add(unavailable);
        products.add(missingFields);
        products.add(null);

        FilterCriteria criteria = new FilterCriteria();
        criteria.setKeyword("copper");
        assertThat(new KeywordFilter().handle(products, criteria)).hasSize(2);
        criteria.setKeyword("  ");
        assertThat(new KeywordFilter().handle(products, criteria)).hasSize(4);

        criteria = new FilterCriteria();
        criteria.setCategory("plumbing");
        assertThat(new CategoryFilter().handle(products, criteria)).hasSize(2);
        criteria.setCategory(" ");
        assertThat(new CategoryFilter().handle(products, criteria)).hasSize(4);

        criteria = new FilterCriteria();
        criteria.setMinPrice(15.0);
        criteria.setMaxPrice(25.0);
        assertThat(new PriceRangeFilter().handle(products, criteria)).hasSize(2);
        criteria.setMinPrice(null);
        criteria.setMaxPrice(null);
        assertThat(new PriceRangeFilter().handle(products, criteria)).hasSize(4);

        criteria = new FilterCriteria();
        criteria.setAvailableOnly(true);
        assertThat(new AvailabilityFilter().handle(products, criteria)).hasSize(2);
        criteria.setAvailableOnly(false);
        assertThat(new AvailabilityFilter().handle(products, criteria)).hasSize(4);

        criteria = new FilterCriteria();
        criteria.setMinRating(4.0);
        assertThat(new RatingFilter().handle(products, criteria)).hasSize(2);
        criteria.setMinRating(null);
        assertThat(new RatingFilter().handle(products, criteria)).hasSize(4);
    }

    @Test
    void filterHandlerChainsAndNullProductsAreHandled() {
        ProductFilterHandler first = new PassThroughHandler();
        ProductFilterHandler second = new PassThroughHandler();
        first.setNextHandler(second);

        List<ProductListing> result = first.handle(null, new FilterCriteria());
        assertThat(result).isEmpty();
    }

    private static final class PassThroughHandler extends ProductFilterHandler {
        @Override
        protected List<ProductListing> applyFilter(List<ProductListing> products, FilterCriteria criteria) {
            return products;
        }
    }
}
