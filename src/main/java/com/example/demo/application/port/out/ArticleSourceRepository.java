package com.example.demo.application.port.out;

import com.example.demo.domain.model.ArticleSource;
import java.util.List;
import java.util.Optional;

public interface ArticleSourceRepository {
    Optional<ArticleSource> findById(String sourceId);
    List<ArticleSource> findAll();
    void saveAll(List<ArticleSource> sources);
}
