package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.PasswordResetRepository;
import com.example.demo.domain.model.PasswordResetRequest;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.PasswordResetEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaPasswordResetRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaPasswordResetAdapter implements PasswordResetRepository {

    private final JpaPasswordResetRepository jpaPasswordResetRepository;

    public JpaPasswordResetAdapter(JpaPasswordResetRepository jpaPasswordResetRepository) {
        this.jpaPasswordResetRepository = jpaPasswordResetRepository;
    }

    @Override
    public Optional<PasswordResetRequest> findByToken(String token) {
        return jpaPasswordResetRepository.findByResetToken(token)
                .map(PasswordResetEntity::toDomain);
    }

    @Override
    public PasswordResetRequest save(PasswordResetRequest request) {
        return jpaPasswordResetRepository.save(PasswordResetEntity.fromDomain(request)).toDomain();
    }
}
