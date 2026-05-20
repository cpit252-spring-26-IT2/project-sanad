package sa.edu.kau.fcit.cpit252.project.reviews;

public class ReviewSummary {
    private final ReviewTargetType targetType;
    private final String targetName;
    private final double averageRating;
    private final int totalReviews;
    private final String visualRating;

    public ReviewSummary(ReviewTargetType targetType, String targetName, double averageRating, int totalReviews) {
        this.targetType = targetType;
        this.targetName = targetName;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.visualRating = buildVisualRating(averageRating);
    }

    public ReviewTargetType getTargetType() {
        return targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public String getVisualRating() {
        return visualRating;
    }

    private String buildVisualRating(double averageRating) {
        int filledStars = (int) Math.floor(averageRating);

        if (filledStars < 0) {
            filledStars = 0;
        }
        if (filledStars > 5) {
            filledStars = 5;
        }

        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < filledStars; i++) {
            stars.append("★");
        }
        for (int i = filledStars; i < 5; i++) {
            stars.append("☆");
        }

        return stars.toString();
    }

    @Override
    public String toString() {
        return targetType + ": " + targetName
                + ", Average: " + averageRating
                + ", Total Reviews: " + totalReviews
                + ", Visual: " + visualRating;
    }
}
