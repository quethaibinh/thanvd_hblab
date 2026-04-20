package com.example.demo.application.port.out;

import com.example.demo.domain.model.ArticleReaction;
import java.util.List;
import java.util.Optional;

public interface ReactionRepository {
    ArticleReaction save(ArticleReaction reaction);
    Optional<ArticleReaction> findByArticleIdAndUserId(String articleId, String userId);
    List<ArticleReaction> findByArticleId(String articleId);
    long countByArticleIdAndType(String articleId, com.example.demo.domain.model.ReactionType type);
    void delete(String reactionId);
}
