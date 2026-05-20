package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {

    private final long tokenValidityHours;
    private final Map<String, TokenRecord> tokens = new ConcurrentHashMap<>();

    public AuthTokenService(@Value("${app.auth.token-validity-hours:24}") long tokenValidityHours) {
        this.tokenValidityHours = tokenValidityHours;
    }

    public String issueToken(Long userId) {
        String token = UUID.randomUUID().toString();
        TokenRecord record = new TokenRecord(userId, Instant.now().plus(tokenValidityHours, ChronoUnit.HOURS));
        tokens.put(token, record);
        return token;
    }

    public Optional<Long> resolveUserId(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        TokenRecord record = tokens.get(token);
        if (record == null) {
            return Optional.empty();
        }
        if (Instant.now().isAfter(record.expiresAt())) {
            tokens.remove(token);
            return Optional.empty();
        }
        return Optional.of(record.userId());
    }

    private record TokenRecord(Long userId, Instant expiresAt) {
    }
}
