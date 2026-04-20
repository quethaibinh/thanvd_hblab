package com.example.demo.infrastructure.adapter.out.persistence.jpa.adapter;

import com.example.demo.application.port.out.ArticleSourceRepository;
import com.example.demo.domain.model.ArticleSource;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.entity.ArticleSourceEntity;
import com.example.demo.infrastructure.adapter.out.persistence.jpa.repository.JpaArticleSourceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaArticleSourceAdapter implements ArticleSourceRepository {

    private final JpaArticleSourceRepository jpaArticleSourceRepository;

    public JpaArticleSourceAdapter(JpaArticleSourceRepository jpaArticleSourceRepository) {
        this.jpaArticleSourceRepository = jpaArticleSourceRepository;
    }

    @Override
    public Optional<ArticleSource> findById(String sourceId) {
        return jpaArticleSourceRepository.findById(sourceId)
                .map(ArticleSourceEntity::toDomain);
    }

    @Override
    public List<ArticleSource> findAll() {
        return jpaArticleSourceRepository.findAll().stream()
                .map(ArticleSourceEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<ArticleSource> findBySlug(String slug) {
        return jpaArticleSourceRepository.findBySlug(slug)
                .map(ArticleSourceEntity::toDomain);
    }

    @Override
    public void saveAll(List<ArticleSource> sources) {
        List<ArticleSourceEntity> entities = sources.stream()
                .map(ArticleSourceEntity::fromDomain)
                .toList();
        jpaArticleSourceRepository.saveAll(entities);
    }
}
