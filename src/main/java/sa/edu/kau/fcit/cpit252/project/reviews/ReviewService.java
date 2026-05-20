package sa.edu.kau.fcit.cpit252.project.reviews;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReviewService {
    private final List<Review> reviews;

    public ReviewService() {
        this.reviews = new ArrayList<>();
    }

    public void addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null.");
        }
        reviews.add(review);
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews);
    }

    public List<Review> getReviewsByTarget(ReviewTargetType targetType, String targetName) {
        List<Review> result = new ArrayList<>();

        if (targetType == null || isBlank(targetName)) {
            return result;
        }

        for (Review review : reviews) {
            if (review.getTargetType() == targetType && review.getTargetName().equalsIgnoreCase(targetName.trim())) {
                result.add(review);
            }
        }

        return result;
    }

    public double calculateAverageRating(ReviewTargetType targetType, String targetName) {
        List<Review> targetReviews = getReviewsByTarget(targetType, targetName);

        if (targetReviews.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Review review : targetReviews) {
            total += review.getRating();
        }

        return (double) total / targetReviews.size();
    }

    public ReviewSummary getSummary(ReviewTargetType targetType, String targetName) {
        List<Review> targetReviews = getReviewsByTarget(targetType, targetName);
        double averageRating = calculateAverageRating(targetType, targetName);
        int totalReviews = targetReviews.size();

        return new ReviewSummary(targetType, targetName, averageRating, totalReviews);
    }

    public List<ReviewSummary> getAllSummaries() {
        List<ReviewSummary> summaries = new ArrayList<>();
        Map<String, List<Review>> groupedReviews = new LinkedHashMap<>();

        for (Review review : reviews) {
            String groupKey = review.getTargetType() + "::" + review.getTargetName().toLowerCase();
            if (!groupedReviews.containsKey(groupKey)) {
                groupedReviews.put(groupKey, new ArrayList<>());
            }
            groupedReviews.get(groupKey).add(review);
        }

        for (List<Review> group : groupedReviews.values()) {
            if (group.isEmpty()) {
                continue;
            }

            Review firstReview = group.get(0);
            int ratingTotal = 0;

            for (Review review : group) {
                ratingTotal += review.getRating();
            }

            double averageRating = (double) ratingTotal / group.size();
            summaries.add(new ReviewSummary(
                    firstReview.getTargetType(),
                    firstReview.getTargetName(),
                    averageRating,
                    group.size()
            ));
        }

        return summaries;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
