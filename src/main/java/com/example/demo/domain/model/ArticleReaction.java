package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Objects;

public record ArticleReaction(
        String id,
        String articleId,
        String userId,
        ReactionType type,
        Instant reactedAt) {

    public ArticleReaction {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(articleId, "articleId must not be null");
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(reactedAt, "reactedAt must not be null");
    }
}
