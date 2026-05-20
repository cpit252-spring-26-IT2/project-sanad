package sa.edu.kau.fcit.cpit252.project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import sa.edu.kau.fcit.cpit252.project.reviews.ReviewTargetType;

public class CreateReviewRequest {
    @NotNull(message = "targetType is required")
    private ReviewTargetType targetType;

    @NotNull(message = "targetId is required")
    private Long targetId;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    public ReviewTargetType getTargetType() { return targetType; }
    public void setTargetType(ReviewTargetType targetType) { this.targetType = targetType; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
