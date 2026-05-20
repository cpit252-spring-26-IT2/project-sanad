package sa.edu.kau.fcit.cpit252.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sa.edu.kau.fcit.cpit252.project.domain.UserRole;
import sa.edu.kau.fcit.cpit252.project.dto.AuthLoginRequest;
import sa.edu.kau.fcit.cpit252.project.dto.AuthRegisterRequest;
import sa.edu.kau.fcit.cpit252.project.dto.AuthResponse;
import sa.edu.kau.fcit.cpit252.project.entity.UserEntity;
import sa.edu.kau.fcit.cpit252.project.repository.ShopRepository;
import sa.edu.kau.fcit.cpit252.project.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ShopRepository shopRepository;
    @Mock
    private AuthTokenService authTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, shopRepository, authTokenService);
    }

    @Test
    void registerRejectsDuplicateAndInvalidRoles() {
        AuthRegisterRequest request = baseRegister();
        when(userRepository.existsByEmailIgnoreCase("customer@sanad.sa")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        when(userRepository.existsByEmailIgnoreCase("customer@sanad.sa")).thenReturn(false);
        request.setRole(null);
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Role is required");

        request.setRole("ADMIN");
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CUSTOMER or SHOP_OWNER");
    }

    @Test
    void registerAndLoginAndMeCoverHappyAndFailurePaths() {
        AuthRegisterRequest register = baseRegister();
        when(userRepository.existsByEmailIgnoreCase("customer@sanad.sa")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(100L);
            return user;
        });
        when(authTokenService.issueToken(100L)).thenReturn("token-100");

        AuthResponse response = authService.register(register);
        assertThat(response.token()).isEqualTo("token-100");
        assertThat(response.user().role()).isEqualTo(UserRole.CUSTOMER);
        assertThat(response.user().email()).isEqualTo("customer@sanad.sa");

        UserEntity stored = new UserEntity();
        stored.setId(200L);
        stored.setName("Customer");
        stored.setEmail("customer@sanad.sa");
        stored.setRole(UserRole.CUSTOMER);
        stored.setPasswordHash(new BCryptPasswordEncoder().encode("customer123"));
        when(userRepository.findByEmailIgnoreCase("customer@sanad.sa")).thenReturn(Optional.of(stored));
        when(authTokenService.issueToken(200L)).thenReturn("token-200");

        AuthLoginRequest loginRequest = new AuthLoginRequest();
        loginRequest.setEmail("customer@sanad.sa");
        loginRequest.setPassword("customer123");
        assertThat(authService.login(loginRequest).token()).isEqualTo("token-200");

        loginRequest.setPassword("wrong");
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email or password");

        when(authTokenService.resolveUserId("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.me("bad"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid or expired token");

        when(authTokenService.resolveUserId("ok")).thenReturn(Optional.of(200L));
        when(userRepository.findById(200L)).thenReturn(Optional.of(stored));
        assertThat(authService.me("ok").id()).isEqualTo(200L);

        when(userRepository.findById(200L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.me("ok"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void registerShopOwnerUsesShopAliasAndDefaults() {
        AuthRegisterRequest request = baseRegister();
        request.setRole("shop");
        request.setShopName("  ");
        request.setName("Owner Name");
        request.setShopCategory(" ");
        request.setShopDescription(" desc ");
        request.setShopContactInfo(" contact ");

        when(userRepository.existsByEmailIgnoreCase("customer@sanad.sa")).thenReturn(false);
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity user = invocation.getArgument(0);
            user.setId(300L);
            return user;
        });
        when(authTokenService.issueToken(300L)).thenReturn("token-300");

        AuthResponse response = authService.register(request);
        assertThat(response.user().role()).isEqualTo(UserRole.SHOP_OWNER);
        assertThat(response.user().name()).isEqualTo("Owner Name Shop");
    }

    private AuthRegisterRequest baseRegister() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setName("Customer");
        request.setEmail("customer@sanad.sa");
        request.setPassword("customer123");
        request.setRole("CUSTOMER");
        return request;
    }
}
