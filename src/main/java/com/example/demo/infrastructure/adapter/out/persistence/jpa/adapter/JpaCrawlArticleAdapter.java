package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.CrawlArticlePort;
import com.example.demo.domain.model.Article;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ArticleEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaArticleRepository;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * JPA adapter implementing {@link CrawlArticlePort}.
 * Handles Phase 3 (Deduplication) and Phase 6 (Storage) of the crawl pipeline.
 */
@Component
public class JpaCrawlArticleAdapter implements CrawlArticlePort {

    private static final Logger log = Logger.getLogger(JpaCrawlArticleAdapter.class.getName());

    private final JpaArticleRepository jpaArticleRepository;

    public JpaCrawlArticleAdapter(JpaArticleRepository jpaArticleRepository) {
        this.jpaArticleRepository = jpaArticleRepository;
    }

    /**
     * Phase 3 — Deduplication check via DB query.
     */
    @Override
    public boolean existsByUrl(String articleUrl) {
        return jpaArticleRepository.existsBySourceArticleUrl(articleUrl);
    }

    /**
     * Phase 6 — Persist a new Article.
     * Returns false if a constraint violation or any exception occurs.
     */
    @Override
    public boolean save(Article article) {
        try {
            jpaArticleRepository.save(ArticleEntity.fromDomain(article));
            return true;
        } catch (Exception e) {
            log.warning("[CrawlArticlePort] Failed to save article url="
                    + article.sourceArticleUrl() + " → " + e.getMessage());
            return false;
        }
    }
}
