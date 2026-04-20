package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.port.in.ArticleQueryUseCase;
import com.example.demo.application.port.in.CommentUseCase;
import com.example.demo.application.port.in.ReactionUseCase;
import com.example.demo.domain.model.ReactionType;
import com.example.demo.infrastructure.security.AuthenticatedUserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleQueryUseCase articleQueryUseCase;
    private final CommentUseCase commentUseCase;
    private final ReactionUseCase reactionUseCase;

    public ArticleController(
            ArticleQueryUseCase articleQueryUseCase,
            CommentUseCase commentUseCase,
            ReactionUseCase reactionUseCase) {
        this.articleQueryUseCase = articleQueryUseCase;
        this.commentUseCase = commentUseCase;
        this.reactionUseCase = reactionUseCase;
    }

    @GetMapping
    public List<ArticleQueryUseCase.ArticleListItemView> listArticles() {
        return articleQueryUseCase.listArticles();
    }

    @GetMapping("/{articleId}")
    public ArticleQueryUseCase.ArticleDetailView getArticleDetail(
            @PathVariable String articleId,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return articleQueryUseCase.getArticleDetail(articleId, principal == null ? null : principal.userId());
    }

    @PostMapping("/{articleId}/comments")
    public ArticleQueryUseCase.CommentView addComment(
            @PathVariable String articleId,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody AddCommentRequest request) {
        return commentUseCase.addComment(principal.userId(), new CommentUseCase.AddCommentCommand(articleId, request.content()));
    }

    @PostMapping("/{articleId}/reactions")
    public ArticleQueryUseCase.ReactionSummaryView react(
            @PathVariable String articleId,
            @AuthenticationPrincipal AuthenticatedUserPrincipal principal,
            @Valid @RequestBody ReactRequest request) {
        return reactionUseCase.react(principal.userId(), new ReactionUseCase.ReactToArticleCommand(articleId, request.reactionType()));
    }

    public record AddCommentRequest(
            @NotBlank(message = "Comment content is required")
            String content) {
    }

    public record ReactRequest(
            @NotNull(message = "Reaction type is required")
            ReactionType reactionType) {
    }
}
