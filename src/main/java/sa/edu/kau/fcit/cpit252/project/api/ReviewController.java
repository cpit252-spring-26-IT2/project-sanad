package sa.edu.kau.fcit.cpit252.project.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import sa.edu.kau.fcit.cpit252.project.dto.CreateReviewRequest;
import sa.edu.kau.fcit.cpit252.project.dto.ReviewSummaryResponse;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;
import sa.edu.kau.fcit.cpit252.project.service.AuthService;
import sa.edu.kau.fcit.cpit252.project.service.ReviewApiService;
import sa.edu.kau.fcit.cpit252.project.support.AuthHeaderUtil;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewApiService reviewApiService;
    private final AuthService authService;

    public ReviewController(ReviewApiService reviewApiService, AuthService authService) {
        this.reviewApiService = reviewApiService;
        this.authService = authService;
    }

    @PostMapping
    public ReviewSummaryResponse createReview(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        Long userId = authService.me(AuthHeaderUtil.bearerToken(authorization)).id();
        return reviewApiService.createReview(userId, request);
    }

    @GetMapping("/summary")
    public ReviewSummaryResponse summary(
            @RequestParam ReviewTargetType targetType,
            @RequestParam Long targetId
    ) {
        return reviewApiService.getSummary(targetType, targetId);
    }
}
