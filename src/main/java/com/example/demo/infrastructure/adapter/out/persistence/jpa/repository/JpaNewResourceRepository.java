package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.NewResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link NewResourceEntity}.
 */
@Repository
public interface JpaNewResourceRepository extends JpaRepository<NewResourceEntity, String> {

    List<NewResourceEntity> findByStatus(String status);

    boolean existsByDomain(String domain);
}
