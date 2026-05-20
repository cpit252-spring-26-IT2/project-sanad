package sa.edu.kau.fcit.cpit252.project.dto;

import sa.edu.kau.fcit.cpit252.project.domain.UserRole;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        UserRole role,
        LocalDateTime createdAt
) {}
