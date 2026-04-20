package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ArticleSourceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaArticleSourceRepository extends JpaRepository<ArticleSourceEntity, String> {
    Optional<ArticleSourceEntity> findBySlug(String slug);
}
