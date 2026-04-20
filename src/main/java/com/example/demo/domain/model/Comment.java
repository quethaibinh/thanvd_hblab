package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record Comment(
        String id,
        String articleId,
        String userId,
        String userDisplayName,
        String content,
        Instant createdAt) {

    public Comment {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(articleId, "articleId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(userDisplayName, "userDisplayName must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
    }
}
