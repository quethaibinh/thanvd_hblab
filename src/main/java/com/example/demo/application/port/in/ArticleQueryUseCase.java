package com.example.demo.application.port.in;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface ArticleQueryUseCase {
    List<ArticleListItemView> listArticles();
    ArticleDetailView getArticleDetail(String articleId, String currentUserId);

    record ArticleListItemView(
            String id,
            String slug,
            String title,
            String summary,
            String thumbnailUrl,
            String category,
            List<String> tags,
            String authorName,
            Instant publishedAt,
            SourceView source) {
    }

    record ArticleDetailView(
            String id,
            String externalId,
            String slug,
            String title,
            String summary,
            String content,
            String thumbnailUrl,
            String category,
            List<String> tags,
            String authorName,
            String canonicalUrl,
            String sourceArticleUrl,
            Instant publishedAt,
            Instant crawledAt,
            SourceView source,
            ReactionSummaryView reactions,
            List<CommentView> comments,
            Map<String, String> metadata) {
    }

    record SourceView(
            String id,
            String name,
            String slug,
            String type,
            String homePageUrl,
            String rssUrl,
            String logoUrl) {
    }

    record ReactionSummaryView(
            Map<String, Long> counts,
            String currentUserReaction,
            long total) {
    }

    record CommentView(
            String id,
            String userId,
            String userDisplayName,
            String content,
            Instant createdAt) {
    }
}
