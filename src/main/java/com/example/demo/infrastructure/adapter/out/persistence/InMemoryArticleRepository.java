package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryArticleRepository implements ArticleRepository {

    private final Map<String, Article> articlesById = new ConcurrentHashMap<>();

    @Override
    public List<Article> findPublishedArticles() {
        return articlesById.values().stream()
                .filter(article -> article.status() == ArticleStatus.PUBLISHED)
                .toList();
    }

    @Override
    public Optional<Article> findPublishedById(String articleId) {
        return Optional.ofNullable(articlesById.get(articleId))
                .filter(article -> article.status() == ArticleStatus.PUBLISHED);
    }

    @Override
    public void saveAll(List<Article> articles) {
        articles.forEach(article -> articlesById.put(article.id(), article));
    }
}
