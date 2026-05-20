package sa.edu.kau.fcit.cpit252.project.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sa.edu.kau.fcit.cpit252.project.domain.UserRole;
import sa.edu.kau.fcit.cpit252.project.dto.CreateReviewRequest;
import sa.edu.kau.fcit.cpit252.project.dto.ReviewSummaryResponse;
import sa.edu.kau.fcit.cpit252.project.dto.UserResponse;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;
import sa.edu.kau.fcit.cpit252.project.service.AuthService;
import sa.edu.kau.fcit.cpit252.project.service.ReviewApiService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerUnitTest {

    @Mock
    private ReviewApiService reviewApiService;
    @Mock
    private AuthService authService;

    private ReviewController controller;

    @BeforeEach
    void setUp() {
        controller = new ReviewController(reviewApiService, authService);
    }

    @Test
    void createReviewAndSummaryDelegateToServices() {
        CreateReviewRequest request = new CreateReviewRequest();
        request.setTargetType(ReviewTargetType.PRODUCT);
        request.setTargetId(7L);
        request.setRating(5);

        UserResponse user = new UserResponse(10L, "Ali", "ali@sanad.sa", UserRole.CUSTOMER, LocalDateTime.now());
        ReviewSummaryResponse response = new ReviewSummaryResponse(ReviewTargetType.PRODUCT, 7L, 4.5, 2, "★★★★☆");

        when(authService.me("token")).thenReturn(user);
        when(reviewApiService.createReview(10L, request)).thenReturn(response);
        when(reviewApiService.getSummary(ReviewTargetType.PRODUCT, 7L)).thenReturn(response);

        ReviewSummaryResponse created = controller.createReview("Bearer token", request);
        assertThat(created).isEqualTo(response);

        ReviewSummaryResponse summary = controller.summary(ReviewTargetType.PRODUCT, 7L);
        assertThat(summary).isEqualTo(response);
    }
}
