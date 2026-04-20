package com.example.demo.infrastructure.adapter.out.persistence;

import com.example.demo.application.port.out.ArticleSourceRepository;
import com.example.demo.domain.model.ArticleSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

//@Repository
public class InMemoryArticleSourceRepository implements ArticleSourceRepository {

    private final Map<String, ArticleSource> sourcesById = new ConcurrentHashMap<>();

    @Override
    public Optional<ArticleSource> findById(String sourceId) {
        return Optional.ofNullable(sourcesById.get(sourceId));
    }

    @Override
    public Optional<ArticleSource> findBySlug(String slug) {
        return sourcesById.values().stream()
                .filter(source -> source.slug().equals(slug))
                .findFirst();
    }

    @Override
    public List<ArticleSource> findAll() {
        return List.copyOf(sourcesById.values());
    }

    @Override
    public void saveAll(List<ArticleSource> sources) {
        sources.forEach(source -> sourcesById.put(source.id(), source));
    }
}
