package sa.edu.kau.fcit.cpit252.project.reviews;

public class Review {
    private final String customerName;
    private final ReviewTargetType targetType;
    private final String targetName;
    private final int rating;

    public Review(String customerName, ReviewTargetType targetType, String targetName, int rating) {
        if (isBlank(customerName)) {
            throw new IllegalArgumentException("Customer name cannot be blank.");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type cannot be null.");
        }
        if (isBlank(targetName)) {
            throw new IllegalArgumentException("Target name cannot be blank.");
        }
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        this.customerName = customerName.trim();
        this.targetType = targetType;
        this.targetName = targetName.trim();
        this.rating = rating;
    }

    public String getCustomerName() {
        return customerName;
    }

    public ReviewTargetType getTargetType() {
        return targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Customer: " + customerName
                + ", Target: " + targetType + " - " + targetName
                + ", Rating: " + rating + "/5";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}