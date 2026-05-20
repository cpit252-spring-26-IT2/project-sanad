package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.stereotype.Service;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.FilterCriteria;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductFilterService;
import sa.edu.kau.fcit.cpit252.project.cor.filtering.ProductListing;
import sa.edu.kau.fcit.cpit252.project.dto.*;
import sa.edu.kau.fcit.cpit252.project.entity.CategoryEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ProductOfferEntity;
import sa.edu.kau.fcit.cpit252.project.repository.CategoryRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ProductOfferRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ProductRepository;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.HighestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.LowestPriceStrategy;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.PriceComparisonService;
import sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.PriceComparisonStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ProductService {

    private final ProductOfferRepository productOfferRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewApiService reviewApiService;

    public ProductService(ProductOfferRepository productOfferRepository,
                          ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ReviewApiService reviewApiService) {
        this.productOfferRepository = productOfferRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.reviewApiService = reviewApiService;
    }

    public PagedResponse<ProductListItemResponse> listProducts(ProductQueryParams params) {
        int page = Math.max(params.getPage(), 1);
        int limit = Math.max(1, Math.min(params.getLimit(), 100));

        List<ProductOfferEntity> offers = productOfferRepository.findAll();
        Map<Long, ReviewApiService.RatingAggregate> ratings = reviewApiService.summarizeProducts();

        String normalizedCategory = normalizeCategory(params.getCategory());
        FilterCriteria criteria = buildCriteria(params, normalizedCategory);

        List<ListingRow> rows = new ArrayList<>();
        for (ProductOfferEntity offer : offers) {
            ReviewApiService.RatingAggregate aggregate = ratings.getOrDefault(
                    offer.getProduct().getId(),
                    new ReviewApiService.RatingAggregate(0.0, 0)
            );

            ProductListing listing = new ProductListing(
                    offer.getProduct().getName(),
                    offer.getProduct().getCategory().getName(),
                    offer.getProduct().getVariant(),
                    offer.getShop().getName(),
                    offer.getPrice().doubleValue(),
                    offer.isAvailable(),
                    aggregate.average()
            );

            rows.add(new ListingRow(listing, offer, aggregate));
        }

        ProductFilterService filterService = new ProductFilterService();
        List<ProductListing> filteredListings = filterService.filterProducts(
                rows.stream().map(ListingRow::listing).toList(),
                criteria
        );

        Set<ProductListing> keep = Collections.newSetFromMap(new IdentityHashMap<>());
        keep.addAll(filteredListings);

        Map<Long, ListingRow> bestPerProduct = new LinkedHashMap<>();
        for (ListingRow row : rows) {
            if (!keep.contains(row.listing())) {
                continue;
            }

            Long productId = row.offer().getProduct().getId();
            ListingRow current = bestPerProduct.get(productId);
            if (current == null || row.offer().getPrice().compareTo(current.offer().getPrice()) < 0) {
                bestPerProduct.put(productId, row);
            }
        }

        List<ListingRow> productRows = new ArrayList<>(bestPerProduct.values());
        productRows.sort(sortComparator(params.getSort()));

        int total = productRows.size();
        int from = Math.min((page - 1) * limit, total);
        int to = Math.min(from + limit, total);

        List<ProductListItemResponse> items = productRows.subList(from, to).stream()
                .map(this::toListItemResponse)
                .toList();

        return new PagedResponse<>(items, page, limit, total);
    }

    public ProductDetailResponse getProduct(Long productId) {
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found."));

        List<ProductOfferEntity> offers = productOfferRepository.findByProductId(productId);
        ProductOfferEntity best = offers.stream()
                .min(Comparator.comparing(ProductOfferEntity::getPrice))
                .orElseThrow(() -> new IllegalArgumentException("No offers found for product."));

        ReviewApiService.RatingAggregate aggregate = reviewApiService.summarizeProducts()
                .getOrDefault(productId, new ReviewApiService.RatingAggregate(0.0, 0));

        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getVariant(),
                product.getDescription(),
                product.getCategory().getName(),
                product.getCategory().getSlug(),
                product.getImageUrl(),
                best.getPrice(),
                best.isAvailable(),
                best.getStockQuantity(),
                aggregate.average(),
                aggregate.totalReviews()
        );
    }

    public List<ProductOfferResponse> getProductOffers(Long productId, String sort) {
        List<ProductOfferEntity> offers = productOfferRepository.findByProductId(productId);
        if (offers.isEmpty()) {
            productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found."));
            return List.of();
        }

        PriceComparisonStrategy strategy = "price_desc".equalsIgnoreCase(sort)
                ? new HighestPriceStrategy()
                : new LowestPriceStrategy();

        PriceComparisonService comparisonService = new PriceComparisonService(strategy);
        List<sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer> virtualOffers = offers.stream()
                .map(o -> new sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer(
                        o.getProduct().getName(),
                        o.getProduct().getVariant(),
                        o.getShop().getName(),
                        o.getPrice().doubleValue()
                ))
                .toList();

        List<sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer> sortedVirtual =
                comparisonService.compareOffers(virtualOffers);

        Map<String, Deque<ProductOfferEntity>> lookup = new HashMap<>();
        for (ProductOfferEntity offer : offers) {
            String key = offerKey(offer.getProduct().getName(), offer.getProduct().getVariant(), offer.getShop().getName(), offer.getPrice());
            lookup.computeIfAbsent(key, k -> new ArrayDeque<>()).add(offer);
        }

        List<ProductOfferEntity> sortedEntities = new ArrayList<>();
        for (sa.edu.kau.fcit.cpit252.project.strategy.price.comparisons.ProductOffer virtual : sortedVirtual) {
            String key = offerKey(virtual.getProductName(), virtual.getVariant(), virtual.getShopName(),
                    BigDecimal.valueOf(virtual.getPrice()));
            Deque<ProductOfferEntity> queue = lookup.get(key);
            if (queue != null && !queue.isEmpty()) {
                sortedEntities.add(queue.pollFirst());
            }
        }

        return sortedEntities.stream()
                .map(this::toOfferResponse)
                .toList();
    }

    public List<ProductOfferResponse> compareOffers(Long productId, String sort) {
        return getProductOffers(productId, sort);
    }

    private ProductListItemResponse toListItemResponse(ListingRow row) {
        ProductOfferEntity offer = row.offer();
        ProductEntity product = offer.getProduct();
        CategoryEntity category = product.getCategory();

        return new ProductListItemResponse(
                product.getId(),
                product.getName(),
                product.getVariant(),
                product.getDescription(),
                category.getName(),
                category.getSlug(),
                product.getImageUrl(),
                offer.getShop().getName(),
                offer.getPrice(),
                offer.isAvailable(),
                offer.getStockQuantity(),
                row.rating().average(),
                row.rating().totalReviews()
        );
    }

    private ProductOfferResponse toOfferResponse(ProductOfferEntity offer) {
        return new ProductOfferResponse(
                offer.getId(),
                offer.getProduct().getId(),
                offer.getProduct().getName(),
                offer.getProduct().getVariant(),
                offer.getShop().getId(),
                offer.getShop().getName(),
                offer.getPrice(),
                offer.isAvailable(),
                offer.getStockQuantity()
        );
    }

    private FilterCriteria buildCriteria(ProductQueryParams params, String normalizedCategory) {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setKeyword(blankToNull(params.getSearch()));
        criteria.setCategory(blankToNull(normalizedCategory));
        criteria.setMinPrice(params.getMinPrice());
        criteria.setMaxPrice(params.getMaxPrice());
        criteria.setAvailableOnly(Boolean.TRUE.equals(params.getAvailableOnly()));
        criteria.setMinRating(params.getMinRating());
        return criteria;
    }

    private String normalizeCategory(String rawCategory) {
        if (isBlank(rawCategory)) {
            return null;
        }

        String value = rawCategory.trim();
        Optional<CategoryEntity> bySlug = categoryRepository.findBySlugIgnoreCase(value);
        if (bySlug.isPresent()) {
            return bySlug.get().getName();
        }

        return value;
    }

    private Comparator<ListingRow> sortComparator(String sort) {
        if ("price_asc".equalsIgnoreCase(sort)) {
            return Comparator.comparing(row -> row.offer().getPrice());
        }
        if ("price_desc".equalsIgnoreCase(sort)) {
            return Comparator.comparing((ListingRow row) -> row.offer().getPrice()).reversed();
        }
        if ("name_asc".equalsIgnoreCase(sort)) {
            return Comparator.comparing(row -> row.offer().getProduct().getName(), String.CASE_INSENSITIVE_ORDER);
        }

        return Comparator.comparing((ListingRow row) -> row.offer().getCreatedAt()).reversed();
    }

    private String offerKey(String productName, String variant, String shopName, BigDecimal price) {
        String normalizedVariant = variant == null ? "" : variant;
        String normalizedPrice = price.setScale(2, RoundingMode.HALF_UP).toPlainString();
        return productName + "|" + normalizedVariant + "|" + shopName + "|" + normalizedPrice;
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private record ListingRow(
            ProductListing listing,
            ProductOfferEntity offer,
            ReviewApiService.RatingAggregate rating
    ) {}
}
