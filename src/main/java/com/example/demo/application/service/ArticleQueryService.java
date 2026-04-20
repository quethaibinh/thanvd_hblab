package com.example.demo.application.service;

import com.example.demo.application.port.in.ArticleQueryUseCase;
import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.application.port.out.ArticleSourceRepository;
import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleReaction;
import com.example.demo.domain.model.ArticleSource;
import com.example.demo.domain.model.Comment;
import com.example.demo.shared.exception.NotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
public class ArticleQueryService implements ArticleQueryUseCase {

    private final ArticleRepository articleRepository;
    private final ArticleSourceRepository articleSourceRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    public ArticleQueryService(
            ArticleRepository articleRepository,
            ArticleSourceRepository articleSourceRepository,
            CommentRepository commentRepository,
            ReactionRepository reactionRepository) {
        this.articleRepository = articleRepository;
        this.articleSourceRepository = articleSourceRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
    }

    @Override
    public List<ArticleListItemView> listArticles() {
        return articleRepository.findPublishedArticles().stream()
                .sorted(Comparator.comparing(Article::publishedAt).reversed())
                .map(article -> new ArticleListItemView(
                        article.id(),
                        article.slug(),
                        article.title(),
                        article.summary(),
                        article.thumbnailUrl(),
                        article.category(),
                        article.tags(),
                        article.authorName(),
                        article.publishedAt(),
                        toSourceView(article.sourceId())))
                .toList();
    }

    @Override
    public ArticleDetailView getArticleDetail(String articleId, String currentUserId) {
        Article article = articleRepository.findPublishedById(articleId)
                .orElseThrow(() -> new NotFoundException("Article not found: " + articleId));
        List<CommentView> comments = commentRepository.findByArticleId(articleId).stream()
                .sorted(Comparator.comparing(Comment::createdAt).reversed())
                .map(comment -> new CommentView(
                        comment.id(),
                        comment.userId(),
                        comment.userDisplayName(),
                        comment.content(),
                        comment.createdAt()))
                .toList();
        List<ArticleReaction> reactions = reactionRepository.findByArticleId(articleId);
        return new ArticleDetailView(
                article.id(),
                article.externalId(),
                article.slug(),
                article.title(),
                article.summary(),
                article.content(),
                article.thumbnailUrl(),
                article.category(),
                article.tags(),
                article.authorName(),
                article.canonicalUrl(),
                article.sourceArticleUrl(),
                article.publishedAt(),
                article.crawledAt(),
                toSourceView(article.sourceId()),
                toReactionSummary(reactions, currentUserId),
                comments,
                article.metadata());
    }

    public ReactionSummaryView getReactionSummary(String articleId, String currentUserId) {
        return toReactionSummary(reactionRepository.findByArticleId(articleId), currentUserId);
    }

    private SourceView toSourceView(String sourceId) {
        ArticleSource source = sourceId == null ? null : articleSourceRepository.findById(sourceId).orElse(null);
        if (source == null) {
            return null;
        }
        return new SourceView(
                source.id(),
                source.name(),
                source.slug(),
                source.type().name(),
                source.homePageUrl(),
                source.rssUrl(),
                source.logoUrl());
    }

    private ReactionSummaryView toReactionSummary(List<ArticleReaction> reactions, String currentUserId) {
        Map<String, Long> counts = Arrays.stream(com.example.demo.domain.model.ReactionType.values())
                .collect(Collectors.toMap(Enum::name, reactionType -> 0L, (left, right) -> left, java.util.LinkedHashMap::new));

        reactions.stream()
                .collect(Collectors.groupingBy(reaction -> reaction.type().name(), () -> new java.util.LinkedHashMap<>(), Collectors.counting()))
                .forEach(counts::put);

        Optional<String> currentUserReaction = reactions.stream()
                .filter(reaction -> reaction.userId().equals(currentUserId))
                .map(reaction -> reaction.type().name())
                .findFirst();

        return new ReactionSummaryView(counts, currentUserReaction.orElse(null), reactions.size());
    }
}
