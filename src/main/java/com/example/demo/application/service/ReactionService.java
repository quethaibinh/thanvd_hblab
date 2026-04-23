package com.example.demo.application.service;

import com.example.demo.application.port.in.ArticleQueryUseCase;
import com.example.demo.application.port.in.ReactionUseCase;
import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.domain.model.ArticleReaction;
import com.example.demo.shared.exception.NotFoundException;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import jakarta.transaction.Transactional;

@Transactional
public class ReactionService implements ReactionUseCase {

    private final ReactionRepository reactionRepository;
    private final ArticleRepository articleRepository;
    private final ArticleQueryService articleQueryService;
    private final Clock clock;

    public ReactionService(
            ReactionRepository reactionRepository,
            ArticleRepository articleRepository,
            ArticleQueryService articleQueryService,
            Clock clock) {
        this.reactionRepository = reactionRepository;
        this.articleRepository = articleRepository;
        this.articleQueryService = articleQueryService;
        this.clock = clock;
    }

    @Override
    public ArticleQueryUseCase.ReactionSummaryView react(String userId, ReactToArticleCommand command) {
        articleRepository.findPublishedById(command.articleId())
                .orElseThrow(() -> new NotFoundException("Article not found: " + command.articleId()));
        ArticleReaction currentReaction = reactionRepository.findByArticleIdAndUserId(command.articleId(), userId)
                .orElse(null);
        ArticleReaction nextReaction = new ArticleReaction(
                currentReaction == null ? UUID.randomUUID().toString() : currentReaction.id(),
                command.articleId(),
                userId,
                command.reactionType(),
                Instant.now(clock));
        reactionRepository.save(nextReaction);
        return articleQueryService.getReactionSummary(command.articleId(), userId);
    }
}
