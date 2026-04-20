package com.example.demo.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record Article(
        String id,
        String externalId,
        String slug,
        String title,
        String summary,
        String content,
        String thumbnailUrl,
        String category,
        List<String> tags,
        String authorName,
        String sourceId,
        String sourceArticleUrl,
        String canonicalUrl,
        Instant publishedAt,
        Instant crawledAt,
        ArticleStatus status,
        Map<String, String> metadata) {

    public Article {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(slug, "slug must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(summary, "summary must not be null");
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(category, "category must not be null");
        Objects.requireNonNull(authorName, "authorName must not be null");
        Objects.requireNonNull(status, "status must not be null");
        tags = tags == null ? List.of() : List.copyOf(tags);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
