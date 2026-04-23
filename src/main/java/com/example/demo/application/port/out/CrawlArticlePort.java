package com.example.demo.application.port.out;

import com.example.demo.domain.model.Article;

/**
 * Output port — Phase 3 (Deduplication) + Phase 6 (Storage).
 * Handles checking for existing articles and persisting new ones.
 */
public interface CrawlArticlePort {

    /**
     * Phase 3 — Check if an article with this URL already exists in the database.
     *
     * @param articleUrl the original article URL
     * @return true if already stored (skip), false if new (proceed)
     */
    boolean existsByUrl(String articleUrl);

    /**
     * Phase 6 — Persist a new article.
     * Should only be called after {@link #existsByUrl} returns false.
     *
     * @param article the fully assembled Article domain object
     * @return true if INSERT succeeded, false on constraint violation or error
     */
    boolean save(Article article);
}
