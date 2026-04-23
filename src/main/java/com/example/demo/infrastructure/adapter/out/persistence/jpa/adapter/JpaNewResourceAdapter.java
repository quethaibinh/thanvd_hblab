package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.NewResourceRepository;
import com.example.demo.domain.model.NewResource;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.NewResourceEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaNewResourceRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * JPA adapter implementing {@link NewResourceRepository}.
 * Translates between domain model {@link NewResource} and JPA entity {@link NewResourceEntity}.
 */
@Component
public class JpaNewResourceAdapter implements NewResourceRepository {

    private final JpaNewResourceRepository jpaRepo;

    public JpaNewResourceAdapter(JpaNewResourceRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public List<NewResource> findByStatus(String status) {
        return jpaRepo.findByStatus(status).stream()
                .map(NewResourceEntity::toDomain)
                .toList();
    }

    public void save(NewResource resource) {
        jpaRepo.save(NewResourceEntity.fromDomain(resource));
    }

    public boolean existsByDomain(String domain) {
        return jpaRepo.existsByDomain(domain);
    }
}
