package com.example.demo.application.port.in;

/**
 * Use case entry point for the RSS crawl pipeline.
 * Invoked by the scheduler or manually via an admin endpoint.
 */
public interface CrawlUseCase {

    /**
     * Crawl all active news sources.
     * Orchestrates all 6 phases of the pipeline for each source.
     */
    void crawlAllSources();

    /**
     * Result summary for a single source crawl run.
     *
     * @param sourceId   ID of the news source
     * @param discovered total feed entries found across all discovered feeds
     * @param saved      number of new articles successfully saved to DB
     * @param skipped    number of entries skipped (already exists in DB)
     */
    record CrawlSourceResult(String sourceId, int discovered, int saved, int skipped) {}
}
