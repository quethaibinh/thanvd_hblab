package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record UserAccount(
        String id,
        String email,
        String passwordHash,
        String fullName,
        String avatarUrl,
        String bio,
        UserRole role,
        boolean active,
        Instant createdAt,
        Instant updatedAt) {

    public UserAccount {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(passwordHash, "passwordHash must not be null");
        Objects.requireNonNull(fullName, "fullName must not be null");
        Objects.requireNonNull(role, "role must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public UserAccount withProfile(String nextFullName, String nextAvatarUrl, String nextBio, Instant nextUpdatedAt) {
        return new UserAccount(
                id,
                email,
                passwordHash,
                nextFullName,
                nextAvatarUrl,
                nextBio,
                role,
                active,
                createdAt,
                nextUpdatedAt);
    }

    public UserAccount withPasswordHash(String nextPasswordHash, Instant nextUpdatedAt) {
        return new UserAccount(
                id,
                email,
                nextPasswordHash,
                fullName,
                avatarUrl,
                bio,
                role,
                active,
                createdAt,
                nextUpdatedAt);
    }
}
