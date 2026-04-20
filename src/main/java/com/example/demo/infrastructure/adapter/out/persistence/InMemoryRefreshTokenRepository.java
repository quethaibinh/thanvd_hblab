package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.RefreshTokenRepository;
import com.example.demo.domain.model.RefreshToken;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {

    private final Map<String, RefreshToken> refreshTokensByToken = new ConcurrentHashMap<>();

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        refreshTokensByToken.put(refreshToken.token(), refreshToken);
        return refreshToken;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(refreshTokensByToken.get(token));
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokensByToken.remove(token);
    }
}
