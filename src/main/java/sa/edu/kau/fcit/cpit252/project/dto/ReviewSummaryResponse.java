package sa.edu.kau.fcit.cpit252.project.dto;

import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

public record ReviewSummaryResponse(
        ReviewTargetType targetType,
        Long targetId,
        double averageRating,
        long totalReviews,
        String visualRating
) {}
