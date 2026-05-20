package sa.edu.kau.fcit.cpit252.project.dto;

import java.math.BigDecimal;

public record ProductDetailResponse(
        Long id,
        String name,
        String variant,
        String description,
        String categoryName,
        String categorySlug,
        String imageUrl,
        BigDecimal bestPrice,
        boolean available,
        int stockQuantity,
        double rating,
        long reviewCount
) {}
