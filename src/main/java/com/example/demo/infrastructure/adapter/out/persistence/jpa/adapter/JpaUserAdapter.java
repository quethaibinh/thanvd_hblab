package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.UserEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaUserRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaUserAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public JpaUserAdapter(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<UserAccount> findById(String id) {
        return jpaUserRepository.findById(id).map(UserEntity::toDomain);
    }

    @Override
    public Optional<UserAccount> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public UserAccount save(UserAccount user) {
        return jpaUserRepository.save(UserEntity.fromDomain(user)).toDomain();
    }
}
