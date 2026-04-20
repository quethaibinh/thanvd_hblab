package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record PasswordResetRequest(
        String id,
        String userId,
        String resetToken,
        Instant createdAt,
        Instant expiresAt) {

    public PasswordResetRequest {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(resetToken, "resetToken must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }
}
