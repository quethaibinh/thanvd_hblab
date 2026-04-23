package com.example.demo.application.service;

import com.example.demo.application.port.in.AuthUseCase;
import com.example.demo.application.port.out.AccessTokenProvider;
import com.example.demo.application.port.out.PasswordHasher;
import com.example.demo.application.port.out.PasswordResetRepository;
import com.example.demo.application.port.out.RefreshTokenRepository;
import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.PasswordResetRequest;
import com.example.demo.domain.model.RefreshToken;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.domain.model.UserRole;
import com.example.demo.shared.exception.ConflictException;
import com.example.demo.shared.exception.NotFoundException;
import com.example.demo.shared.exception.UnauthorizedException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import jakarta.transaction.Transactional;

@Transactional
public class AuthService implements AuthUseCase {

    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(7);
    private static final Duration RESET_TOKEN_DURATION = Duration.ofHours(2);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final PasswordHasher passwordHasher;
    private final AccessTokenProvider accessTokenProvider;
    private final Clock clock;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetRepository passwordResetRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            Clock clock) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordHasher = passwordHasher;
        this.accessTokenProvider = accessTokenProvider;
        this.clock = clock;
    }

    @Override
    public AuthResult register(RegisterCommand command) {
        String normalizedEmail = normalizeEmail(command.email());
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("Email already exists");
        }

        Instant now = Instant.now(clock);
        UserAccount user = new UserAccount(
                UUID.randomUUID().toString(),
                normalizedEmail,
                passwordHasher.hash(command.password()),
                command.fullName().trim(),
                null,
                null,
                UserRole.READER,
                true,
                now,
                now);
        userRepository.save(user);
        return createAuthResult(user, now);
    }

    @Override
    public AuthResult login(LoginCommand command) {
        String normalizedEmail = normalizeEmail(command.email());
        UserAccount user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordHasher.matches(command.password(), user.passwordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        return createAuthResult(user, Instant.now(clock));
    }

    @Override
    public ForgotPasswordResult forgotPassword(ForgotPasswordCommand command) {
        String normalizedEmail = normalizeEmail(command.email());
        UserAccount user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + normalizedEmail));
        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(RESET_TOKEN_DURATION);
        String token = "reset-" + UUID.randomUUID();
        passwordResetRepository.save(new PasswordResetRequest(
                UUID.randomUUID().toString(),
                user.id(),
                token,
                now,
                expiresAt));
        return new ForgotPasswordResult(
                user.email(),
                token,
                expiresAt,
                "Demo mode: use this token on your future reset-password screen.");
    }

    @Override
    public AuthResult refresh(RefreshTokenCommand command) {
        Instant now = Instant.now(clock);
        RefreshToken currentRefreshToken = refreshTokenRepository.findByToken(command.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (currentRefreshToken.isExpired(now)) {
            refreshTokenRepository.deleteByToken(currentRefreshToken.token());
            throw new UnauthorizedException("Refresh token has expired");
        }

        UserAccount user = userRepository.findById(currentRefreshToken.userId())
                .orElseThrow(() -> new UnauthorizedException("User not found for refresh token"));
        refreshTokenRepository.deleteByToken(currentRefreshToken.token());
        return createAuthResult(user, now);
    }

    private AuthResult createAuthResult(UserAccount user, Instant now) {
        Instant accessTokenExpiresAt = now.plus(ACCESS_TOKEN_DURATION);
        Instant refreshTokenExpiresAt = now.plus(REFRESH_TOKEN_DURATION);
        String accessToken = accessTokenProvider.issueToken(
                user.id(),
                user.email(),
                user.role().name(),
                now,
                accessTokenExpiresAt);
        String refreshToken = "refresh-" + UUID.randomUUID();
        refreshTokenRepository.save(new RefreshToken(refreshToken, user.id(), now, refreshTokenExpiresAt));
        return new AuthResult(
                user.id(),
                user.email(),
                user.fullName(),
                user.role().name(),
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpiresAt,
                refreshTokenExpiresAt);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
