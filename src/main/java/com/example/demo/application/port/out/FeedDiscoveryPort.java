package com.example.demo.application.port.out;

import java.util.List;

/**
 * Output port — Phase 1: Feed Discovery.
 * Scrapes the general RSS page of a news source to find direct feed URLs.
 * Example: "https://vnexpress.net/rss" → ["https://vnexpress.net/rss/tin-moi-nhat.rss", ...]
 */
public interface FeedDiscoveryPort {

    /**
     * Discover all RSS/Atom feed URLs from a general RSS aggregation page.
     *
     * @param rssPageUrl the general RSS overview URL (e.g. https://vnexpress.net/rss)
     * @return list of direct feed URLs pointing to XML content
     */
    List<String> discoverFeedUrls(String rssPageUrl);
}
