package com.example.demo.application.port.out;

import java.time.Instant;

public interface AccessTokenProvider {
    String issueToken(String userId, String email, String role, Instant issuedAt, Instant expiresAt);
}
