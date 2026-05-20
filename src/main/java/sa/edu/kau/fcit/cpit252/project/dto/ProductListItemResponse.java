package sa.edu.kau.fcit.cpit252.project.dto;

import java.math.BigDecimal;

public record ProductListItemResponse(
        Long id,
        String name,
        String variant,
        String description,
        String categoryName,
        String categorySlug,
        String imageUrl,
        String bestOfferShop,
        BigDecimal price,
        boolean available,
        int stockQuantity,
        double rating,
        long reviewCount
) {}
