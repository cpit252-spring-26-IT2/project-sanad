package sa.edu.kau.fcit.cpit252.project.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import sa.edu.kau.fcit.cpit252.project.dto.*;
import sa.edu.kau.fcit.cpit252.project.service.AuthService;
import sa.edu.kau.fcit.cpit252.project.support.AuthHeaderUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody AuthRegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthLoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(@RequestHeader("Authorization") String authorization) {
        return authService.me(AuthHeaderUtil.bearerToken(authorization));
    }
}
