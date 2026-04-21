package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.port.in.AuthUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    public AuthUseCase.AuthResult register(@Valid @RequestBody RegisterRequest request) {
        return authUseCase.register(new AuthUseCase.RegisterCommand(
                request.email(),
                request.password(),
                request.fullName()));
    }

    @PostMapping("/login")
    public AuthUseCase.AuthResult login(@Valid @RequestBody LoginRequest request) {
        return authUseCase.login(new AuthUseCase.LoginCommand(request.email(), request.password()));
    }

    @PostMapping("/forgot-password")
    public AuthUseCase.ForgotPasswordResult forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return authUseCase.forgotPassword(new AuthUseCase.ForgotPasswordCommand(request.email()));
    }

    @PostMapping("/refresh")
    public AuthUseCase.AuthResult refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authUseCase.refresh(new AuthUseCase.RefreshTokenCommand(request.refreshToken()));
    }

    public record RegisterRequest(
            @Email(message = "Email is invalid") @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must contain at least 6 characters") String password,
            @NotBlank(message = "Full name is required") String fullName) {
    }

    public record LoginRequest(
            @Email(message = "Email is invalid") @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Password is required") String password) {
    }

    public record ForgotPasswordRequest(
            @Email(message = "Email is invalid") @NotBlank(message = "Email is required") String email) {
    }

    public record RefreshTokenRequest(
            @NotBlank(message = "Refresh token is required") String refreshToken) {
    }
}
