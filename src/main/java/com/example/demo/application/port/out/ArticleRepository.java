package com.example.demo.application.port.out;

import com.example.demo.domain.model.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    PageResult<Article> findPublishedArticles(int page, int size);

    record PageResult<T>(List<T> content, long totalElements) {}

    Optional<Article> findPublishedById(String articleId);

    void saveAll(List<Article> articles);

    Boolean existsBySourceArticleUrl(String articleURL);
}
