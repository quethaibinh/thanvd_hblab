package com.example.demo.application.port.out;

import com.example.demo.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
}
