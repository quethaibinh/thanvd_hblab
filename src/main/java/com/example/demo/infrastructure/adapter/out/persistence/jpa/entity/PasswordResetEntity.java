package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.PasswordResetRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "password_reset_requests")
public class PasswordResetEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String resetToken;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    public PasswordResetEntity() {
    }

    public static PasswordResetEntity fromDomain(PasswordResetRequest domain) {
        PasswordResetEntity entity = new PasswordResetEntity();
        entity.id = domain.id();
        entity.userId = domain.userId();
        entity.resetToken = domain.resetToken();
        entity.createdAt = domain.createdAt();
        entity.expiresAt = domain.expiresAt();
        return entity;
    }

    public PasswordResetRequest toDomain() {
        return new PasswordResetRequest(id, userId, resetToken, createdAt, expiresAt);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
