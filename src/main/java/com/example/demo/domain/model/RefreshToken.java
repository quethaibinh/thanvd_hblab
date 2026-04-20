package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record RefreshToken(
        String token,
        String userId,
        Instant createdAt,
        Instant expiresAt) {

    public RefreshToken {
        Objects.requireNonNull(token, "token must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
}
