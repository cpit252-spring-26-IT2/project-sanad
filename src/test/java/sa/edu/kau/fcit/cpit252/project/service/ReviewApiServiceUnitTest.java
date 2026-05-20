package sa.edu.kau.fcit.cpit252.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewApiServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ShopRepository shopRepository;

    private ReviewApiService service;

    @BeforeEach
    void setUp() {
        service = new ReviewApiService(reviewRepository, userRepository, productRepository, shopRepository);
    }

    @Test
    void createReviewValidatesRatingAndCustomer() {
        CreateReviewRequest invalid = request(ReviewTargetType.PRODUCT, 1L, 6);
        assertThatThrownBy(() -> service.createReview(9L, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 5");

        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createReview(9L, request(ReviewTargetType.PRODUCT, 1L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Customer account not found");
    }

    @Test
    void createReviewEnsuresTargetsAndSummaries() {
        UserEntity customer = new UserEntity();
        customer.setId(9L);
        when(userRepository.findById(9L)).thenReturn(Optional.of(customer));
        when(reviewRepository.save(any(ReviewEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createReview(9L, request(ReviewTargetType.PRODUCT, 2L, 4)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");

        ProductEntity product = new ProductEntity();
        product.setId(2L);
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(reviewRepository.findByTargetTypeAndTargetId(ReviewTargetType.PRODUCT, 2L)).thenReturn(List.of(
                entity(5),
                entity(4)
        ));
        ReviewSummaryResponse productSummary = service.createReview(9L, request(ReviewTargetType.PRODUCT, 2L, 5));
        assertThat(productSummary.averageRating()).isEqualTo(4.5);
        assertThat(productSummary.totalReviews()).isEqualTo(2);

        when(shopRepository.findById(3L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.createReview(9L, request(ReviewTargetType.SHOP, 3L, 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Shop not found");

        ShopEntity shop = new ShopEntity();
        shop.setId(3L);
        when(shopRepository.findById(3L)).thenReturn(Optional.of(shop));
        when(reviewRepository.findByTargetTypeAndTargetId(ReviewTargetType.SHOP, 3L)).thenReturn(List.of(entity(3)));
        ReviewSummaryResponse shopSummary = service.createReview(9L, request(ReviewTargetType.SHOP, 3L, 3));
        assertThat(shopSummary.averageRating()).isEqualTo(3.0);
        assertThat(shopSummary.visualRating()).isEqualTo("★★★☆☆");
    }

    @Test
    void summaryAndAggregateHandleNullsAndRound() {
        assertThatThrownBy(() -> service.getSummary(null, 1L))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getSummary(ReviewTargetType.PRODUCT, null))
                .isInstanceOf(IllegalArgumentException.class);

        when(reviewRepository.findByTargetTypeAndTargetId(ReviewTargetType.PRODUCT, 55L)).thenReturn(List.of(
                entity(5), entity(4), entity(4)
        ));
        ReviewSummaryResponse summary = service.getSummary(ReviewTargetType.PRODUCT, 55L);
        assertThat(summary.averageRating()).isEqualTo(4.3);
        assertThat(summary.totalReviews()).isEqualTo(3);

        when(reviewRepository.summarizeByTargetType(ReviewTargetType.PRODUCT)).thenReturn(List.<Object[]>of(
                new Object[]{55L, 4.25d, 8L}
        ));
        assertThat(service.summarizeProducts())
                .containsKey(55L);
        assertThat(service.summarizeProducts().get(55L).average()).isEqualTo(4.3);
    }

    private static CreateReviewRequest request(ReviewTargetType type, Long targetId, Integer rating) {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setTargetType(type);
        request.setTargetId(targetId);
        request.setRating(rating);
        return request;
    }

    private static ReviewEntity entity(int rating) {
        ReviewEntity entity = new ReviewEntity();
        entity.setRating(rating);
        return entity;
    }
}
