package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.RefreshTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByToken(String token);
}
