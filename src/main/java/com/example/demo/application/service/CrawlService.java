package com.example.demo.application.service;

import com.example.demo.application.port.in.CrawlUseCase;
import com.example.demo.application.port.out.ArticleContentPort;
import com.example.demo.application.port.out.CrawlArticlePort;
import com.example.demo.application.port.out.FeedDiscoveryPort;
import com.example.demo.application.port.out.NewResourceRepository;
import com.example.demo.application.port.out.RssFeedPort;
import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleStatus;
import com.example.demo.domain.model.NewResource;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;
import jakarta.transaction.Transactional;

/**
 * Application service that orchestrates the full 6-phase RSS crawl pipeline.
 *
 * <pre>
 * Phase 1 - Discovery     : FeedDiscoveryPort  → scrape RSS page → list of feed URLs
 * Phase 2 - Feed Parsing  : RssFeedPort        → fetch feed XML → list of RawFeedEntry
 * Phase 3 - Deduplication : CrawlArticlePort   → check existsByUrl → skip if exists
 * Phase 4 - Metadata      : inline in service  → normalize author, thumbnail, date, summary
 * Phase 5 - Content       : ArticleContentPort → fetch article URL → clean HTML body
 * Phase 6 - Storage       : CrawlArticlePort   → save Article to DB
 * </pre>
 */
public class CrawlService implements CrawlUseCase {

    private static final Logger log = Logger.getLogger(CrawlService.class.getName());

    /** Maximum characters kept for the article summary (from feed description). */
    private static final int MAX_SUMMARY_LENGTH = 1000;

    /** Fallback author name when feed entry does not contain one. */
    private static final String UNKNOWN_AUTHOR = "Unknown";

    private final NewResourceRepository newResourceRepository;
    private final FeedDiscoveryPort feedDiscoveryPort;
    private final RssFeedPort rssFeedPort;
    private final ArticleContentPort articleContentPort;
    private final CrawlArticlePort crawlArticlePort;
    private final Clock clock;

    public CrawlService(
            NewResourceRepository newResourceRepository,
            FeedDiscoveryPort feedDiscoveryPort,
            RssFeedPort rssFeedPort,
            ArticleContentPort articleContentPort,
            CrawlArticlePort crawlArticlePort,
            Clock clock) {
        this.newResourceRepository = newResourceRepository;
        this.feedDiscoveryPort = feedDiscoveryPort;
        this.rssFeedPort = rssFeedPort;
        this.articleContentPort = articleContentPort;
        this.crawlArticlePort = crawlArticlePort;
        this.clock = clock;
    }

    // -------------------------------------------------------------------------
    // CrawlUseCase
    // -------------------------------------------------------------------------

    @Override
    public void crawlAllSources() {
        List<NewResource> activeSources = newResourceRepository.findByStatus("ACTIVE");
        log.info("[Crawler] Starting crawl for " + activeSources.size() + " active sources");

        for (NewResource source : activeSources) {
            try {
                CrawlSourceResult result = crawlSource(source);
                log.info("[Crawler] Source=" + source.name()
                        + " discovered=" + result.discovered()
                        + " saved=" + result.saved()
                        + " skipped=" + result.skipped());
            } catch (Exception e) {
                log.severe("[Crawler] Error crawling source=" + source.name() + " → " + e.getMessage());
            }
        }
    }

    // -------------------------------------------------------------------------
    // Internal pipeline
    // -------------------------------------------------------------------------

    private CrawlSourceResult crawlSource(NewResource source) {
        int discovered = 0;
        int saved = 0;
        int skipped = 0;

        // ── Phase 1: Feed Discovery ────────────────────────────────────────────
        List<String> feedUrls = feedDiscoveryPort.discoverFeedUrls(source.rss_url());
        log.info("[Crawler][Discovery] source=" + source.name()
                + " rssPage=" + source.rss_url()
                + " feedsFound=" + feedUrls.size());

        for (String feedUrl : feedUrls) {
            // ── Phase 2: Feed Parsing ──────────────────────────────────────────
            List<RssFeedPort.RawFeedEntry> entries = rssFeedPort.fetchFeed(feedUrl);
            discovered += entries.size();

            for (RssFeedPort.RawFeedEntry entry : entries) {
                String articleUrl = entry.link();
                if (articleUrl == null || articleUrl.isBlank()) {
                    skipped++;
                    continue;
                }

                // ── Phase 3: Deduplication ─────────────────────────────────────
                if (crawlArticlePort.existsByUrl(articleUrl)) {
                    skipped++;
                    continue;
                }

                try {
                    // ── Phase 4: Metadata Parsing ──────────────────────────────
                    String author      = normalizeAuthor(entry.author());
                    String thumbnail   = entry.thumbnailUrl();
                    Instant publishedAt = normalizePublishedAt(entry.publishedAt());
                    String summary     = normalizeSummary(entry.description());

                    // ── Phase 5: Content Extraction ────────────────────────────
                    String contentHtml = articleContentPort
                            .extractContent(articleUrl)
                            .orElse("");   // empty string if extraction fails

                    // ── Phase 6: Storage ───────────────────────────────────────
                    Article article = buildArticle(
                            entry, source, author, thumbnail,
                            publishedAt, summary, contentHtml);

                    boolean ok = crawlArticlePort.save(article);
                    if (ok) {
                        saved++;
                    } else {
                        skipped++;
                    }

                } catch (Exception e) {
                    log.warning("[Crawler][Entry] Failed processing url=" + articleUrl
                            + " → " + e.getMessage());
                    skipped++;
                }
            }
        }

        return new CrawlSourceResult(source.id(), discovered, saved, skipped);
    }

    // -------------------------------------------------------------------------
    // Phase 4 helpers — Metadata normalization
    // -------------------------------------------------------------------------

    private String normalizeAuthor(String raw) {
        if (raw == null || raw.isBlank()) return UNKNOWN_AUTHOR;
        return raw.trim();
    }

    private Instant normalizePublishedAt(ZonedDateTime zdt) {
        if (zdt == null) return Instant.now(clock);
        return zdt.toInstant();
    }

    private String normalizeSummary(String raw) {
        if (raw == null || raw.isBlank()) return "";
        // Strip HTML tags that may appear in description
        String plain = raw.replaceAll("<[^>]*>", "").trim();
        if (plain.length() > MAX_SUMMARY_LENGTH) {
            return plain.substring(0, MAX_SUMMARY_LENGTH);
        }
        return plain;
    }

    // -------------------------------------------------------------------------
    // Build Article domain object
    // -------------------------------------------------------------------------

    private Article buildArticle(
            RssFeedPort.RawFeedEntry entry,
            NewResource source,
            String author,
            String thumbnail,
            Instant publishedAt,
            String summary,
            String contentHtml) {

        String id    = UUID.randomUUID().toString();
        String title = entry.title() == null ? "" : entry.title().trim();
        String slug  = buildSlug(title, id);
        String guid  = entry.guid() != null ? entry.guid() : entry.link();

        return new Article(
                id,
                guid,               // externalId ← guid from RSS <guid> tag
                slug,
                title,
                summary.isEmpty() ? title : summary,  // summary must not be null
                contentHtml.isEmpty() ? title : contentHtml, // content must not be null
                thumbnail,
                "General",          // category — RSS feeds rarely expose category; default value
                List.of(),          // tags
                author,
                source.id(),        // sourceId
                entry.link(),       // sourceArticleUrl
                entry.link(),       // canonicalUrl (same as source until overridden)
                publishedAt,
                Instant.now(clock), // crawledAt
                ArticleStatus.PUBLISHED,
                java.util.Map.of(
                        "origin", "rss-crawl",
                        "domain", source.domain()
                )
        );
    }

    /**
     * Generate a URL-safe slug from the article title.
     * Appends a short UUID suffix to guarantee uniqueness.
     */
    private String buildSlug(String title, String id) {
        String base = title.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .trim()
                .replaceAll("\\s+", "-");
        // keep at most 80 chars from title part
        if (base.length() > 80) base = base.substring(0, 80);
        // short suffix from UUID
        String suffix = id.replace("-", "").substring(0, 8);
        return base.isEmpty() ? suffix : base + "-" + suffix;
    }
}
