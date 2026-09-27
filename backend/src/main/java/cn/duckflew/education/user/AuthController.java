package cn.duckflew.education.user;

import cn.duckflew.education.common.api.ApiResponse;
import cn.duckflew.education.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final VerificationService verificationService;

    public AuthController(AuthService authService, VerificationService verificationService) {
        this.authService = authService;
        this.verificationService = verificationService;
    }

    @PostMapping("/send-code")
    public ApiResponse<Void> sendCode(@Valid @RequestBody SendCodeRequest request) {
        verificationService.sendCode(request.target(), request.channel(), request.purpose());
        return ApiResponse.ok();
    }

    @PostMapping("/register")
    public ApiResponse<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }
}
