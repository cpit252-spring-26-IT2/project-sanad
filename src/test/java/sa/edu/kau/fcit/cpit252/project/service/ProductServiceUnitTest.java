package sa.edu.kau.fcit.cpit252.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sa.edu.kau.fcit.cpit252.project.dto.PagedResponse;
import sa.edu.kau.fcit.cpit252.project.dto.ProductListItemResponse;
import sa.edu.kau.fcit.cpit252.project.dto.ProductOfferResponse;
import sa.edu.kau.fcit.cpit252.project.dto.ProductQueryParams;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductOfferEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ShopEntity;
import sa.edu.kau.fcit.cpit252.project.repository.CategoryRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ProductOfferRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ProductRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductOfferRepository productOfferRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ReviewApiService reviewApiService;

    private ProductService service;

    @BeforeEach
    void setUp() {
        service = new ProductService(productOfferRepository, productRepository, categoryRepository, reviewApiService);
    }

    @Test
    void listProductsSupportsSortCategoryNormalizationAndPaging() {
        ProductOfferEntity copperLow = offer(1L, "Copper Pipe", "3/4", "Plumbing", "plumbing", 20L, "Shop A", "20.00", true, 9, LocalDateTime.of(2026, 1, 1, 0, 0));
        ProductOfferEntity copperHigh = offer(2L, "Copper Pipe", "3/4", "Plumbing", "plumbing", 21L, "Shop B", "25.00", true, 4, LocalDateTime.of(2026, 1, 2, 0, 0));
        ProductOfferEntity paint = offer(3L, "Paint Bucket", "White", "Paint", "paint", 22L, "Shop C", "70.00", true, 3, LocalDateTime.of(2026, 1, 3, 0, 0));

        when(productOfferRepository.findAll()).thenReturn(List.of(copperLow, copperHigh, paint));
        when(reviewApiService.summarizeProducts()).thenReturn(Map.of(
                1L, new ReviewApiService.RatingAggregate(4.6, 5),
                3L, new ReviewApiService.RatingAggregate(4.9, 3)
        ));

        CategoryEntity plumbingCategory = new CategoryEntity();
        plumbingCategory.setName("Plumbing");
        when(categoryRepository.findBySlugIgnoreCase("plumbing")).thenReturn(Optional.of(plumbingCategory));

        ProductQueryParams params = new ProductQueryParams();
        params.setCategory("plumbing");
        params.setSort("price_asc");
        params.setAvailableOnly(true);
        params.setPage(1);
        params.setLimit(10);

        PagedResponse<ProductListItemResponse> response = service.listProducts(params);
        assertThat(response.total()).isEqualTo(2);
        assertThat(response.items()).hasSize(2);
        assertThat(response.items().get(0).name()).isEqualTo("Copper Pipe");
        assertThat(response.items().get(0).price()).isEqualByComparingTo("20.00");

        params.setSort("name_asc");
        assertThat(service.listProducts(params).items().get(0).name()).isEqualTo("Copper Pipe");

        params.setSort("newest");
        params.setPage(0);
        params.setLimit(0);
        assertThat(service.listProducts(params).page()).isEqualTo(1);
        assertThat(service.listProducts(params).limit()).isEqualTo(1);
    }

    @Test
    void productLookupAndOfferComparisonHandleEdgeCases() {
        ProductEntity product = offer(1L, "Copper Pipe", "3/4", "Plumbing", "plumbing", 20L, "Shop A", "20.00", true, 9, LocalDateTime.now())
                .getProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductOfferEntity low = offer(1L, "Copper Pipe", "3/4", "Plumbing", "plumbing", 20L, "Shop A", "20.00", true, 9, LocalDateTime.now());
        ProductOfferEntity high = offer(1L, "Copper Pipe", "3/4", "Plumbing", "plumbing", 21L, "Shop B", "35.00", false, 1, LocalDateTime.now());
        when(productOfferRepository.findByProductId(1L)).thenReturn(List.of(high, low));
        when(reviewApiService.summarizeProducts()).thenReturn(Map.of(1L, new ReviewApiService.RatingAggregate(4.2, 10)));

        assertThat(service.getProduct(1L).bestPrice()).isEqualByComparingTo("20.00");
        assertThat(service.getProduct(1L).reviewCount()).isEqualTo(10);

        List<ProductOfferResponse> asc = service.getProductOffers(1L, "price_asc");
        List<ProductOfferResponse> desc = service.getProductOffers(1L, "price_desc");
        assertThat(asc.get(0).price()).isEqualByComparingTo("20.00");
        assertThat(desc.get(0).price()).isEqualByComparingTo("35.00");
        assertThat(service.compareOffers(1L, "price_asc")).hasSize(2);

        when(productOfferRepository.findByProductId(9L)).thenReturn(List.of());
        when(productRepository.findById(9L)).thenReturn(Optional.of(product));
        assertThat(service.getProductOffers(9L, "price_asc")).isEmpty();

        when(productRepository.findById(404L)).thenReturn(Optional.empty());
        when(productOfferRepository.findByProductId(404L)).thenReturn(List.of());
        assertThatThrownBy(() -> service.getProductOffers(404L, "price_asc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");

        when(productRepository.findById(8L)).thenReturn(Optional.of(product));
        when(productOfferRepository.findByProductId(8L)).thenReturn(List.of());
        assertThatThrownBy(() -> service.getProduct(8L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No offers found");
    }

    private static ProductOfferEntity offer(
            Long productId,
            String productName,
            String variant,
            String categoryName,
            String categorySlug,
            Long shopId,
            String shopName,
            String price,
            boolean available,
            int stock,
            LocalDateTime createdAt
    ) {
        CategoryEntity category = new CategoryEntity();
        category.setId(11L);
        category.setName(categoryName);
        category.setSlug(categorySlug);

        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setName(productName);
        product.setVariant(variant);
        product.setDescription("Description");
        product.setCategory(category);
        product.setImageUrl("img");

        ShopEntity shop = new ShopEntity();
        shop.setId(shopId);
        shop.setName(shopName);

        ProductOfferEntity offer = new ProductOfferEntity();
        offer.setId(productId * 100 + shopId);
        offer.setProduct(product);
        offer.setShop(shop);
        offer.setPrice(new BigDecimal(price));
        offer.setAvailable(available);
        offer.setStockQuantity(stock);
        offer.setCreatedAt(createdAt);
        return offer;
    }
}
