package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleStatus;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "articles")
public class ArticleEntity {

    @Id
    private String id;

    private String externalId;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String thumbnailUrl;

    private String category;

    @ElementCollection
    @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(nullable = false)
    private String authorName;

    private String sourceId;

    private String sourceArticleUrl;

    private String canonicalUrl;

    private Instant publishedAt;

    private Instant crawledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status;

    @ElementCollection
    @CollectionTable(name = "article_metadata", joinColumns = @JoinColumn(name = "article_id"))
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    private Map<String, String> metadata = new HashMap<>();

    public ArticleEntity() {
    }

    public static ArticleEntity fromDomain(Article domain) {
        ArticleEntity entity = new ArticleEntity();
        entity.id = domain.id();
        entity.externalId = domain.externalId();
        entity.slug = domain.slug();
        entity.title = domain.title();
        entity.summary = domain.summary();
        entity.content = domain.content();
        entity.thumbnailUrl = domain.thumbnailUrl();
        entity.category = domain.category();
        entity.tags = new ArrayList<>(domain.tags());
        entity.authorName = domain.authorName();
        entity.sourceId = domain.sourceId();
        entity.sourceArticleUrl = domain.sourceArticleUrl();
        entity.canonicalUrl = domain.canonicalUrl();
        entity.publishedAt = domain.publishedAt();
        entity.crawledAt = domain.crawledAt();
        entity.status = domain.status();
        entity.metadata = new HashMap<>(domain.metadata());
        return entity;
    }

    public Article toDomain() {
        return new Article(
                id,
                externalId,
                slug,
                title,
                summary,
                content,
                thumbnailUrl,
                category,
                tags,
                authorName,
                sourceId,
                sourceArticleUrl,
                canonicalUrl,
                publishedAt,
                crawledAt,
                status,
                metadata);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getSourceArticleUrl() { return sourceArticleUrl; }
    public void setSourceArticleUrl(String sourceArticleUrl) { this.sourceArticleUrl = sourceArticleUrl; }
    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Instant getCrawledAt() { return crawledAt; }
    public void setCrawledAt(Instant crawledAt) { this.crawledAt = crawledAt; }
    public ArticleStatus getStatus() { return status; }
    public void setStatus(ArticleStatus status) { this.status = status; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
