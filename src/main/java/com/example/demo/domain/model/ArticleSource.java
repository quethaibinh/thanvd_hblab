package com.example.demo.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public record ArticleSource(
        String id,
        String name,
        String slug,
        ArticleSourceType type,
        String homePageUrl,
        String rssUrl,
        String logoUrl,
        boolean active,
        Instant createdAt,
        Map<String, String> metadata) {

    public ArticleSource {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(slug, "slug must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(createdAt, "createdAt must not be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
