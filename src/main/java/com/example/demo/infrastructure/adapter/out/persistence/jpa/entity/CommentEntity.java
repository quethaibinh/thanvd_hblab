package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "comments")
public class CommentEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String articleId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String userDisplayName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant createdAt;

    public CommentEntity() {
    }

    public static CommentEntity fromDomain(Comment domain) {
        CommentEntity entity = new CommentEntity();
        entity.id = domain.id();
        entity.articleId = domain.articleId();
        entity.userId = domain.userId();
        entity.userDisplayName = domain.userDisplayName();
        entity.content = domain.content();
        entity.createdAt = domain.createdAt();
        return entity;
    }

    public Comment toDomain() {
        return new Comment(
                id,
                articleId,
                userId,
                userDisplayName,
                content,
                createdAt);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getArticleId() { return articleId; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserDisplayName() { return userDisplayName; }
    public void setUserDisplayName(String userDisplayName) { this.userDisplayName = userDisplayName; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
