package com.example.demo.application.port.in;

import com.example.demo.domain.model.ReactionType;

public interface ReactionUseCase {
    ArticleQueryUseCase.ReactionSummaryView react(String userId, ReactToArticleCommand command);

    record ReactToArticleCommand(String articleId, ReactionType reactionType) {
    }
}
