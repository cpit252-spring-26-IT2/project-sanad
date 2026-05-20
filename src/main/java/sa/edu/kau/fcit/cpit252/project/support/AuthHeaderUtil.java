package sa.edu.kau.fcit.cpit252.project.support;

public final class AuthHeaderUtil {

    private AuthHeaderUtil() {
    }

    public static String bearerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.isBlank()) {
            throw new IllegalStateException("Authorization token is required.");
        }

        String prefix = "Bearer ";
        if (!authorizationHeader.startsWith(prefix)) {
            throw new IllegalStateException("Authorization header must use Bearer token.");
        }

        return authorizationHeader.substring(prefix.length()).trim();
    }
}
