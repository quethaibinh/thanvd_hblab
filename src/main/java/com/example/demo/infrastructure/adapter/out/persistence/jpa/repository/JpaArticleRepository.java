package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.domain.model.ArticleStatus;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ArticleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaArticleRepository extends JpaRepository<ArticleEntity, String> {
    List<ArticleEntity> findByStatus(ArticleStatus status);

    Optional<ArticleEntity> findByIdAndStatus(String id, ArticleStatus status);

    boolean existsBySourceArticleUrl(String sourceArticleUrl);
}
