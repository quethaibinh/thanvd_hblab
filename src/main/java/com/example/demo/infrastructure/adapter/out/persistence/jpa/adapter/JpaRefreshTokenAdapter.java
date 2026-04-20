package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.RefreshTokenRepository;
import com.example.demo.domain.model.RefreshToken;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.RefreshTokenEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaRefreshTokenRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaRefreshTokenAdapter implements RefreshTokenRepository {

    private final JpaRefreshTokenRepository jpaRefreshTokenRepository;

    public JpaRefreshTokenAdapter(JpaRefreshTokenRepository jpaRefreshTokenRepository) {
        this.jpaRefreshTokenRepository = jpaRefreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRefreshTokenRepository.findByToken(token)
                .map(RefreshTokenEntity::toDomain);
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return jpaRefreshTokenRepository.save(RefreshTokenEntity.fromDomain(token)).toDomain();
    }

    @Override
    public void deleteByToken(String token) {
        jpaRefreshTokenRepository.deleteByToken(token);
    }
}
