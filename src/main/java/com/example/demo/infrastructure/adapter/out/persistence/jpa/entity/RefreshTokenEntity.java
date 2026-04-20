package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.RefreshToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {

    @Id
    private String token;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    public RefreshTokenEntity() {
    }

    public static RefreshTokenEntity fromDomain(RefreshToken domain) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.token = domain.token();
        entity.userId = domain.userId();
        entity.createdAt = domain.createdAt();
        entity.expiresAt = domain.expiresAt();
        return entity;
    }

    public RefreshToken toDomain() {
        return new RefreshToken(token, userId, createdAt, expiresAt);
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
