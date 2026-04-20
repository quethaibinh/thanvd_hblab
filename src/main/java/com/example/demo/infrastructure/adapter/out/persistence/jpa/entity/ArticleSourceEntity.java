package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.ArticleSource;
import com.example.demo.domain.model.ArticleSourceType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "article_sources")
public class ArticleSourceEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleSourceType type;

    private String homePageUrl;

    private String rssUrl;

    private String logoUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "article_source_metadata", joinColumns = @JoinColumn(name = "source_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata = new HashMap<>();

    public ArticleSourceEntity() {
    }

    public static ArticleSourceEntity fromDomain(ArticleSource domain) {
        ArticleSourceEntity entity = new ArticleSourceEntity();
        entity.id = domain.id();
        entity.name = domain.name();
        entity.slug = domain.slug();
        entity.type = domain.type();
        entity.homePageUrl = domain.homePageUrl();
        entity.rssUrl = domain.rssUrl();
        entity.logoUrl = domain.logoUrl();
        entity.active = domain.active();
        entity.createdAt = domain.createdAt();
        entity.metadata = new HashMap<>(domain.metadata());
        return entity;
    }

    public ArticleSource toDomain() {
        return new ArticleSource(
                id,
                name,
                slug,
                type,
                homePageUrl,
                rssUrl,
                logoUrl,
                active,
                createdAt,
                metadata);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public ArticleSourceType getType() { return type; }
    public void setType(ArticleSourceType type) { this.type = type; }
    public String getHomePageUrl() { return homePageUrl; }
    public void setHomePageUrl(String homePageUrl) { this.homePageUrl = homePageUrl; }
    public String getRssUrl() { return rssUrl; }
    public void setRssUrl(String rssUrl) { this.rssUrl = rssUrl; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
