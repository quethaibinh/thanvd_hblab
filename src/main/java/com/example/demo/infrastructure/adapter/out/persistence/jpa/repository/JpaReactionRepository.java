package com.example.demo.infrastructure.adapter.out.persistence.jpa.repository;

import com.example.demo.domain.model.ReactionType;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ReactionEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReactionRepository extends JpaRepository<ReactionEntity, String> {
    List<ReactionEntity> findByArticleId(String articleId);
    Optional<ReactionEntity> findByArticleIdAndUserId(String articleId, String userId);
    long countByArticleIdAndType(String articleId, ReactionType type);
}
