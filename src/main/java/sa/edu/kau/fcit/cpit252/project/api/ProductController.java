package sa.edu.kau.fcit.cpit252.project.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sa.edu.kau.fcit.cpit252.project.dto.*;
import sa.edu.kau.fcit.cpit252.project.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public PagedResponse<ProductListItemResponse> listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean availableOnly,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "12") int limit
    ) {
        ProductQueryParams params = new ProductQueryParams();
        params.setSearch(search);
        params.setCategory(category);
        params.setMinPrice(minPrice);
        params.setMaxPrice(maxPrice);
        params.setAvailableOnly(availableOnly);
        params.setMinRating(minRating);
        params.setSort(sort);
        params.setPage(page);
        params.setLimit(limit);
        return productService.listProducts(params);
    }

    @GetMapping("/products/{id}")
    public ProductDetailResponse productById(@PathVariable("id") Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/products/{id}/offers")
    public List<ProductOfferResponse> offers(
            @PathVariable("id") Long id,
            @RequestParam(required = false, defaultValue = "price_asc") String sort
    ) {
        return productService.getProductOffers(id, sort);
    }

    @GetMapping("/compare")
    public List<ProductOfferResponse> compare(
            @RequestParam Long productId,
            @RequestParam(required = false, defaultValue = "price_asc") String sort
    ) {
        return productService.compareOffers(productId, sort);
    }
}
