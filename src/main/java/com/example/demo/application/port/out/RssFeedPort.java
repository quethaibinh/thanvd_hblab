package com.example.demo.application.port.out;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Output port — Phase 2: Feed Parsing.
 * Fetches and parses an RSS/Atom XML feed URL into a list of raw entries.
 */
public interface RssFeedPort {

    /**
     * Fetch and parse an RSS or Atom feed.
     *
     * @param feedUrl direct URL to the RSS/Atom XML file
     * @return list of parsed feed entries (empty list on failure)
     */
    List<RawFeedEntry> fetchFeed(String feedUrl);

    /**
     * Represents a single item from a parsed RSS/Atom feed.
     *
     * @param guid         unique identifier from the &lt;guid&gt; tag (may be null)
     * @param title        article title
     * @param link         URL to the original article page
     * @param description  raw summary/description text (may contain HTML snippets)
     * @param thumbnailUrl URL to a thumbnail/cover image (may be null)
     * @param author       author name (may be null)
     * @param publishedAt  publication datetime with timezone (may be null)
     */
    record RawFeedEntry(
            String guid,
            String title,
            String link,
            String description,
            String thumbnailUrl,
            String author,
            ZonedDateTime publishedAt
    ) {}
}
