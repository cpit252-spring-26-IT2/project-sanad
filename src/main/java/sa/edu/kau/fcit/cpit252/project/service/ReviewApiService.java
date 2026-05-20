package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.edu.kau.fcit.cpit252.project.dto.CreateReviewRequest;
import sa.edu.kau.fcit.cpit252.project.dto.ReviewSummaryResponse;
import sa.edu.kau.fcit.cpit252.project.entity.ProductEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ReviewEntity;
import sa.edu.kau.fcit.cpit252.project.entity.ShopEntity;
import sa.edu.kau.fcit.cpit252.project.entity.UserEntity;
import sa.edu.kau.fcit.cpit252.project.repository.ProductRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ReviewRepository;
import sa.edu.kau.fcit.cpit252.project.repository.ShopRepository;
import sa.edu.kau.fcit.cpit252.project.repository.UserRepository;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewSummary;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewApiService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public ReviewApiService(ReviewRepository reviewRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            ShopRepository shopRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    @Transactional
    public ReviewSummaryResponse createReview(Long userId, CreateReviewRequest request) {
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        UserEntity customer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Customer account not found."));

        ensureTargetExists(request.getTargetType(), request.getTargetId());

        ReviewEntity review = new ReviewEntity();
        review.setCustomer(customer);
        review.setTargetType(request.getTargetType());
        review.setTargetId(request.getTargetId());
        review.setRating(request.getRating());
        reviewRepository.save(review);

        return getSummary(request.getTargetType(), request.getTargetId());
    }

    public ReviewSummaryResponse getSummary(ReviewTargetType targetType, Long targetId) {
        if (targetType == null || targetId == null) {
            throw new IllegalArgumentException("targetType and targetId are required.");
        }

        List<ReviewEntity> reviews = reviewRepository.findByTargetTypeAndTargetId(targetType, targetId);
        double average = reviews.stream().mapToInt(ReviewEntity::getRating).average().orElse(0.0);
        ReviewSummary summary = new ReviewSummary(targetType, targetType + " #" + targetId, average, reviews.size());

        return new ReviewSummaryResponse(
                targetType,
                targetId,
                round(average),
                reviews.size(),
                summary.getVisualRating()
        );
    }

    public Map<Long, RatingAggregate> summarizeProducts() {
        return summarizeByType(ReviewTargetType.PRODUCT);
    }

    private Map<Long, RatingAggregate> summarizeByType(ReviewTargetType targetType) {
        Map<Long, RatingAggregate> result = new LinkedHashMap<>();
        for (Object[] row : reviewRepository.summarizeByTargetType(targetType)) {
            Long targetId = ((Number) row[0]).longValue();
            double avg = ((Number) row[1]).doubleValue();
            long count = ((Number) row[2]).longValue();
            result.put(targetId, new RatingAggregate(round(avg), count));
        }
        return result;
    }

    private void ensureTargetExists(ReviewTargetType targetType, Long targetId) {
        if (targetType == ReviewTargetType.PRODUCT) {
            ProductEntity product = productRepository.findById(targetId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found for review."));
            if (product.getId() == null) {
                throw new IllegalArgumentException("Product not found for review.");
            }
            return;
        }

        ShopEntity shop = shopRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found for review."));
        if (shop.getId() == null) {
            throw new IllegalArgumentException("Shop not found for review.");
        }
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record RatingAggregate(double average, long totalReviews) {
    }
}
