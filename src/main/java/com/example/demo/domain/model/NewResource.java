package com.example.demo.domain.model;

import java.util.Objects;

public record NewResource(
        String id,
        String name,
        String domain,
        String rss_url,
        Double reliability_score,
        String status
) {

    public NewResource{
        Objects.requireNonNull(id, "id much not be null");
        Objects.requireNonNull(name, "name much not be null");
        Objects.requireNonNull(domain, "domain much not be null");
        Objects.requireNonNull(rss_url, "rss_url much not be null");
        Objects.requireNonNull(reliability_score, "reliability_score much not be null");
        Objects.requireNonNull(status, "status much not be null");
    }

}
