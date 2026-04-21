package com.example.demo.application.port.in;

import java.time.Instant;

public interface AuthUseCase {
        AuthResult register(RegisterCommand command);

        AuthResult login(LoginCommand command);

        ForgotPasswordResult forgotPassword(ForgotPasswordCommand command);

        AuthResult refresh(RefreshTokenCommand command);

        record RegisterCommand(String email, String password, String fullName) {
        }

        record LoginCommand(String email, String password) {
        }

        record ForgotPasswordCommand(String email) {
        }

        record RefreshTokenCommand(String refreshToken) {
        }

        record AuthResult(
                        String userId,
                        String email,
                        String fullName,
                        String role,
                        String accessToken,
                        String refreshToken,
                        String tokenType,
                        Instant accessTokenExpiresAt,
                        Instant refreshTokenExpiresAt) {
        }

        record ForgotPasswordResult(
                        String email,
                        String resetToken,
                        Instant expiresAt,
                        String message) {
        }
}
