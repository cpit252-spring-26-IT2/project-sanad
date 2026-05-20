package sa.edu.kau.fcit.cpit252.project.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {}
