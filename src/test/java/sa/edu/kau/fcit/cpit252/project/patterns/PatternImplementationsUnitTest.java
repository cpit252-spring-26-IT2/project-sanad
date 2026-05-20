package sa.edu.kau.fcit.cpit252.project.patterns;

import org.junit.jupiter.api.Test;
import sa.edu.kau.fcit.cpit252.project.composite.categories.ToolCategory;
import sa.edu.kau.fcit.cpit252.project.composite.categories.ToolItem;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.FilterCriteria;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductFilterService;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductListing;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.Account;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.CustomerAccount;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.ShopAccount;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.ShopAccountFactory;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.HighestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.LowestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.PriceComparisonService;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PatternImplementationsUnitTest {

    @Test
    void factoryProductsExposeExpectedFields() {
        CustomerAccount customer = new CustomerAccount("Ali", "ali@sanad.sa", "secret");
        assertThat(customer.getAccountType()).isEqualTo("CUSTOMER");
        assertThat(customer.getName()).isEqualTo("Ali");
        assertThat(customer.getUsername()).isEqualTo("ali@sanad.sa");
        assertThat(customer.getPassword()).isEqualTo("secret");
        assertThat(customer.toString()).contains("Account Type: CUSTOMER");

        Account shopAccount = new ShopAccountFactory("BuildPro", "shop@sanad.sa", "pw", "Tools").createAccount();
        assertThat(shopAccount).isInstanceOf(ShopAccount.class);
        assertThat(shopAccount.getAccountType()).isEqualTo("SHOP_OWNER");
        assertThat(shopAccount.toString()).contains("Shop Category: Tools");
    }

    @Test
    void compositeCategoryAggregatesPriceAndTree() {
        ToolItem tile = new ToolItem("Tile", "Ceramic", 20.0);
        ToolItem pipe = new ToolItem("Pipe", "Copper", 15.0);
        ToolCategory plumbing = new ToolCategory("Plumbing");
        plumbing.addComponent(pipe);
        ToolCategory store = new ToolCategory("Store");
        store.addComponent(tile);
        store.addComponent(plumbing);

        assertThat(tile.getName()).isEqualTo("Tile");
        assertThat(tile.getVariant()).isEqualTo("Ceramic");
        assertThat(tile.getPrice()).isEqualTo(20.0);
        assertThat(tile.getAllCategories()).isEmpty();
        assertThat(tile.toString()).contains("SAR 20.0");

        assertThat(store.getName()).isEqualTo("Store");
        assertThat(store.getVariant()).isEqualTo("Multiple Options Inside");
        assertThat(store.getPrice()).isEqualTo(35.0);
        assertThat(store.getAllCategories()).contains("Tile", "Plumbing");
        assertThat(store.toString()).contains("[Store]").contains("[Plumbing]");
    }

    @Test
    void strategyAndChainFilteringHandleEdgeCases() {
        List<ProductOffer> offers = List.of(
                new ProductOffer("Pipe", "Copper", "A", 25.0),
                new ProductOffer("Pipe", "Copper", "B", 15.0),
                new ProductOffer("Tile", "Ceramic", "C", 30.0)
        );

        PriceComparisonService service = new PriceComparisonService(new LowestPriceStrategy());
        assertThat(service.compareOffers(null)).isEmpty();
        assertThat(service.compareOffers(offers).get(0).getShopName()).isEqualTo("B");

        service.setStrategy(new HighestPriceStrategy());
        assertThat(service.compareOffers(offers).get(0).getShopName()).isEqualTo("C");
        assertThatThrownBy(() -> service.setStrategy(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PriceComparisonService(null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(new LowestPriceStrategy().sortOffers(null)).isEmpty();
        assertThat(new LowestPriceStrategy().sortOffers(List.of())).isEmpty();
        assertThat(new HighestPriceStrategy().sortOffers(null)).isEmpty();
        assertThat(new HighestPriceStrategy().sortOffers(List.of())).isEmpty();

        List<ProductOffer> nullableOffers = new ArrayList<>();
        nullableOffers.add(offers.get(0));
        nullableOffers.add(offers.get(1));
        nullableOffers.add(null);
        Map<String, List<ProductOffer>> grouped = service.compareOffersByProduct(nullableOffers);
        assertThat(grouped).containsKey("Pipe (Copper)");
        assertThat(grouped.get("Pipe (Copper)")).hasSize(2);
        assertThat(service.compareOffersByProduct(List.of())).isEmpty();

        ProductFilterService filterService = new ProductFilterService();
        FilterCriteria criteria = new FilterCriteria();
        criteria.setKeyword("copper");
        criteria.setCategory("Plumbing");
        criteria.setMinPrice(10.0);
        criteria.setMaxPrice(30.0);
        criteria.setAvailableOnly(true);
        criteria.setMinRating(4.0);

        List<ProductListing> filtered = filterService.filterProducts(List.of(
                new ProductListing("Copper Pipe", "Plumbing", "1/2", "Shop A", 25.0, true, 4.2),
                new ProductListing("Paint", "Paint", "White", "Shop B", 70.0, true, 4.9)
        ), criteria);
        assertThat(filtered).hasSize(1);
        assertThat(filtered.get(0).toString()).contains("Copper Pipe");
        assertThat(filterService.filterProducts(null, criteria)).isEmpty();
        assertThat(filterService.filterProducts(List.of(filtered.get(0)), null)).hasSize(1);
    }
}
