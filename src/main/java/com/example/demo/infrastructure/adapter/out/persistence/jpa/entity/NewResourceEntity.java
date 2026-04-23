package com.example.demo.infrastructure.adapter.out.persistence.jpa.entity;

import com.example.demo.domain.model.NewResource;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity mapping to the {@code new_resources} table.
 * Stores metadata about external news sources used by the RSS crawler.
 */
@Entity
@Table(name = "new_resources")
public class NewResourceEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domain;

    /** General RSS overview page URL (e.g. https://vnexpress.net/rss). */
    @Column(name = "rss_url", nullable = false)
    private String rssUrl;

    @Column(name = "reliability_score", nullable = false)
    private Double reliabilityScore;

    @Column(nullable = false)
    private String status;

    public NewResourceEntity() {}

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    public static NewResourceEntity fromDomain(NewResource domain) {
        NewResourceEntity entity = new NewResourceEntity();
        entity.id               = domain.id();
        entity.name             = domain.name();
        entity.domain           = domain.domain();
        entity.rssUrl           = domain.rss_url();
        entity.reliabilityScore = domain.reliability_score();
        entity.status           = domain.status();
        return entity;
    }

    public NewResource toDomain() {
        return new NewResource(id, name, domain, rssUrl, reliabilityScore, status);
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getRssUrl() { return rssUrl; }
    public void setRssUrl(String rssUrl) { this.rssUrl = rssUrl; }

    public Double getReliabilityScore() { return reliabilityScore; }
    public void setReliabilityScore(Double reliabilityScore) { this.reliabilityScore = reliabilityScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
