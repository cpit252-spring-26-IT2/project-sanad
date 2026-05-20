package sa.edu.kau.fcit.cpit252.project.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sa.edu.kau.fcit.cpit252.project.domain.UserRole;
import sa.edu.kau.fcit.cpit252.project.dto.*;
import sa.edu.kau.fcit.cpit252.project.entity.ShopEntity;
import sa.edu.kau.fcit.cpit252.project.entity.UserEntity;
import sa.edu.kau.fcit.cpit252.project.factory.accounts.*;
import sa.edu.kau.fcit.cpit252.project.repository.ShopRepository;
import sa.edu.kau.fcit.cpit252.project.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final AuthTokenService authTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository,
                       ShopRepository shopRepository,
                       AuthTokenService authTokenService) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.authTokenService = authTokenService;
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        UserRole role = parseRole(request.getRole());
        Account createdAccount = createAccountFromFactory(request, role);

        UserEntity user = new UserEntity();
        user.setName(createdAccount.getName());
        user.setEmail(createdAccount.getUsername().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(createdAccount.getPassword()));
        user.setRole(role);
        user = userRepository.save(user);

        if (role == UserRole.SHOP_OWNER) {
            ShopEntity shop = new ShopEntity();
            shop.setOwner(user);
            shop.setName(normalizeShopName(request));
            shop.setCategory(blankToNull(request.getShopCategory()));
            shop.setDescription(blankToNull(request.getShopDescription()));
            shop.setContactInfo(blankToNull(request.getShopContactInfo()));
            shopRepository.save(shop);
        }

        String token = authTokenService.issueToken(user.getId());
        return new AuthResponse(token, toUserResponse(user));
    }

    public AuthResponse login(AuthLoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = authTokenService.issueToken(user.getId());
        return new AuthResponse(token, toUserResponse(user));
    }

    public UserResponse me(String token) {
        Long userId = authTokenService.resolveUserId(token)
                .orElseThrow(() -> new IllegalStateException("Invalid or expired token."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found."));

        return toUserResponse(user);
    }

    private Account createAccountFromFactory(AuthRegisterRequest request, UserRole role) {
        if (role == UserRole.CUSTOMER) {
            AccountFactory factory = new CustomerAccountFactory(
                    request.getName().trim(),
                    request.getEmail().trim().toLowerCase(),
                    request.getPassword()
            );
            return factory.createAccount();
        }

        AccountFactory factory = new ShopAccountFactory(
                normalizeShopName(request),
                request.getEmail().trim().toLowerCase(),
                request.getPassword(),
                defaultIfBlank(request.getShopCategory(), "General")
        );
        return factory.createAccount();
    }

    private UserRole parseRole(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Role is required.");
        }

        String normalized = value.trim().toUpperCase();
        if ("SHOP".equals(normalized)) {
            normalized = "SHOP_OWNER";
        }

        try {
            return UserRole.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role must be CUSTOMER or SHOP_OWNER.");
        }
    }

    private String normalizeShopName(AuthRegisterRequest request) {
        if (!isBlank(request.getShopName())) {
            return request.getShopName().trim();
        }
        return request.getName().trim() + " Shop";
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    private String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
