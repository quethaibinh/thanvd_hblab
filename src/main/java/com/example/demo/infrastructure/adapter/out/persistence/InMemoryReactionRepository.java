package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.domain.model.ArticleReaction;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryReactionRepository implements ReactionRepository {

    private final Map<String, ArticleReaction> reactionsById = new ConcurrentHashMap<>();

    @Override
    public ArticleReaction save(ArticleReaction reaction) {
        reactionsById.values().removeIf(existing -> existing.articleId().equals(reaction.articleId())
                && existing.userId().equals(reaction.userId())
                && !existing.id().equals(reaction.id()));
        reactionsById.put(reaction.id(), reaction);
        return reaction;
    }

    @Override
    public List<ArticleReaction> findByArticleId(String articleId) {
        return reactionsById.values().stream()
                .filter(reaction -> reaction.articleId().equals(articleId))
                .toList();
    }

    @Override
    public Optional<ArticleReaction> findByArticleIdAndUserId(String articleId, String userId) {
        return reactionsById.values().stream()
                .filter(reaction -> reaction.articleId().equals(articleId) && reaction.userId().equals(userId))
                .findFirst();
    }

    @Override
    public long countByArticleIdAndType(String articleId, com.example.demo.domain.model.ReactionType type) {
        return reactionsById.values().stream()
                .filter(reaction -> reaction.articleId().equals(articleId) && reaction.type() == type)
                .count();
    }

    @Override
    public void delete(String reactionId) {
        reactionsById.remove(reactionId);
    }
}
