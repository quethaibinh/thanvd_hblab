package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleStatus;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ArticleEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaArticleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaArticleAdapter implements ArticleRepository {

    private final JpaArticleRepository jpaArticleRepository;

    public JpaArticleAdapter(JpaArticleRepository jpaArticleRepository) {
        this.jpaArticleRepository = jpaArticleRepository;
    }

    @Override
    public List<Article> findPublishedArticles() {
        return jpaArticleRepository.findByStatus(ArticleStatus.PUBLISHED).stream()
                .map(ArticleEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Article> findPublishedById(String articleId) {
        return jpaArticleRepository.findByIdAndStatus(articleId, ArticleStatus.PUBLISHED)
                .map(ArticleEntity::toDomain);
    }

    @Override
    public void saveAll(List<Article> articles) {
        List<ArticleEntity> entities = articles.stream()
                .map(ArticleEntity::fromDomain)
                .toList();
        jpaArticleRepository.saveAll(entities);
    }

    @Override
    public Boolean existsBySourceArticleUrl(String articleURL) {
        return jpaArticleRepository.existsBySourceArticleUrl(articleURL);
    }
}
