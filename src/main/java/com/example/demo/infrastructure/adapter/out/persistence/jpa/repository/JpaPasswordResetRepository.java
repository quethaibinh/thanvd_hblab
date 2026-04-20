package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.PasswordResetEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPasswordResetRepository extends JpaRepository<PasswordResetEntity, String> {
    Optional<PasswordResetEntity> findByResetToken(String token);
}
