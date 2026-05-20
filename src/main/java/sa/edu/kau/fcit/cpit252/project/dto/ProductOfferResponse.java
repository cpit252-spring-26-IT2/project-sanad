package sa.edu.kau.fcit.cpit252.project.dto;

import java.math.BigDecimal;

public record ProductOfferResponse(
        Long id,
        Long productId,
        String productName,
        String variant,
        Long shopId,
        String shopName,
        BigDecimal price,
        boolean available,
        int stockQuantity
) {}
