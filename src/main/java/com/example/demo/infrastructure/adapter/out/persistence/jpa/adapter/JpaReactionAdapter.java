package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.domain.model.ArticleReaction;
import com.example.demo.domain.model.ReactionType;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ReactionEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaReactionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaReactionAdapter implements ReactionRepository {

    private final JpaReactionRepository jpaReactionRepository;

    public JpaReactionAdapter(JpaReactionRepository jpaReactionRepository) {
        this.jpaReactionRepository = jpaReactionRepository;
    }

    @Override
    public List<ArticleReaction> findByArticleId(String articleId) {
        return jpaReactionRepository.findByArticleId(articleId).stream()
                .map(ReactionEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ArticleReaction> findByArticleIdAndUserId(String articleId, String userId) {
        return jpaReactionRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ReactionEntity::toDomain);
    }

    @Override
    public long countByArticleIdAndType(String articleId, ReactionType type) {
        return jpaReactionRepository.countByArticleIdAndType(articleId, type);
    }

    @Override
    public ArticleReaction save(ArticleReaction reaction) {
        return jpaReactionRepository.save(ReactionEntity.fromDomain(reaction)).toDomain();
    }

    @Override
    public void delete(String reactionId) {
        jpaReactionRepository.deleteById(reactionId);
    }
}
