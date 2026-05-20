package sa.edu.kau.fcit.cpit252.project.reviews;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewDomainUnitTest {

    @Test
    void reviewValidatesInputsAndToString() {
        assertThatThrownBy(() -> new Review(" ", ReviewTargetType.PRODUCT, "Copper Pipe", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer name");
        assertThatThrownBy(() -> new Review("Ali", null, "Copper Pipe", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target type");
        assertThatThrownBy(() -> new Review("Ali", ReviewTargetType.PRODUCT, " ", 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target name");
        assertThatThrownBy(() -> new Review("Ali", ReviewTargetType.PRODUCT, "Copper Pipe", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 5");

        Review review = new Review(" Ali ", ReviewTargetType.PRODUCT, " Copper Pipe ", 4);
        assertThat(review.getCustomerName()).isEqualTo("Ali");
        assertThat(review.getTargetName()).isEqualTo("Copper Pipe");
        assertThat(review.getTargetType()).isEqualTo(ReviewTargetType.PRODUCT);
        assertThat(review.getRating()).isEqualTo(4);
        assertThat(review.toString()).contains("Customer: Ali").contains("Rating: 4/5");
    }

    @Test
    void reviewServiceCoversHappyPathAndEdgeCases() {
        ReviewService service = new ReviewService();
        assertThatThrownBy(() -> service.addReview(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");

        Review r1 = new Review("A", ReviewTargetType.PRODUCT, "Copper Pipe", 5);
        Review r2 = new Review("B", ReviewTargetType.PRODUCT, "Copper Pipe", 3);
        Review r3 = new Review("C", ReviewTargetType.SHOP, "Toney Flooring", 4);
        service.addReview(r1);
        service.addReview(r2);
        service.addReview(r3);

        List<Review> all = service.getAllReviews();
        assertThat(all).hasSize(3);
        all.clear();
        assertThat(service.getAllReviews()).hasSize(3);

        assertThat(service.getReviewsByTarget(null, "x")).isEmpty();
        assertThat(service.getReviewsByTarget(ReviewTargetType.PRODUCT, " ")).isEmpty();
        assertThat(service.getReviewsByTarget(ReviewTargetType.PRODUCT, " copper pipe "))
                .hasSize(2);

        assertThat(service.calculateAverageRating(ReviewTargetType.PRODUCT, "Copper Pipe"))
                .isEqualTo(4.0);
        assertThat(service.calculateAverageRating(ReviewTargetType.PRODUCT, "Missing"))
                .isEqualTo(0.0);

        ReviewSummary summary = service.getSummary(ReviewTargetType.PRODUCT, "Copper Pipe");
        assertThat(summary.getTargetName()).isEqualTo("Copper Pipe");
        assertThat(summary.getAverageRating()).isEqualTo(4.0);
        assertThat(summary.getTotalReviews()).isEqualTo(2);
        assertThat(summary.getVisualRating()).isEqualTo("★★★★☆");

        List<ReviewSummary> summaries = service.getAllSummaries();
        assertThat(summaries).hasSize(2);
    }

    @Test
    void reviewSummaryBuildsVisualRatingAcrossBounds() {
        ReviewSummary negative = new ReviewSummary(ReviewTargetType.PRODUCT, "A", -1.0, 0);
        ReviewSummary middle = new ReviewSummary(ReviewTargetType.PRODUCT, "B", 3.8, 5);
        ReviewSummary high = new ReviewSummary(ReviewTargetType.SHOP, "C", 9.9, 1);

        assertThat(negative.getVisualRating()).isEqualTo("☆☆☆☆☆");
        assertThat(middle.getVisualRating()).isEqualTo("★★★☆☆");
        assertThat(high.getVisualRating()).isEqualTo("★★★★★");
        assertThat(middle.toString()).contains("Average: 3.8");
    }
}
