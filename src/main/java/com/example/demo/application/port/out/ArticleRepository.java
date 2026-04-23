package com.example.demo.application.port.out;

import com.example.demo.domain.model.Article;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {
    List<Article> findPublishedArticles();

    Optional<Article> findPublishedById(String articleId);

    void saveAll(List<Article> articles);

    Boolean existsBySourceArticleUrl(String articleURL);
}
