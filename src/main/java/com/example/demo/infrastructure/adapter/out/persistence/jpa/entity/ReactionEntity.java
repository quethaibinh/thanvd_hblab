package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.ArticleReaction;
import com.example.demo.domain.model.ReactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
    name = "article_reactions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"articleId", "userId"})
)
public class ReactionEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String articleId;

    @Column(nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType type;

    @Column(nullable = false)
    private Instant reactedAt;

    public ReactionEntity() {
    }

    public static ReactionEntity fromDomain(ArticleReaction domain) {
        ReactionEntity entity = new ReactionEntity();
        entity.id = domain.id();
        entity.articleId = domain.articleId();
        entity.userId = domain.userId();
        entity.type = domain.type();
        entity.reactedAt = domain.reactedAt();
        return entity;
    }

    public ArticleReaction toDomain() {
        return new ArticleReaction(
                id,
                articleId,
                userId,
                type,
                reactedAt);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public ReactionType getType() { return type; }
    public void setType(ReactionType type) { this.type = type; }
    public Instant getReactedAt() { return reactedAt; }
    public void setReactedAt(Instant reactedAt) { this.reactedAt = reactedAt; }
}
