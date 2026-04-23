package com.example.demo.application.port.out;

import java.util.Optional;

/**
 * Output port — Phase 5: Content Extraction.
 * Fetches an article page and extracts the clean HTML body content.
 * The result includes only meaningful content tags (p, ul, ol, img, h1-h6)
 * and excludes scripts, styles, navigation, ads, and boilerplate.
 */
public interface ArticleContentPort {

    /**
     * Fetch the article page and extract its clean HTML body content.
     *
     * @param articleUrl direct URL of the article page
     * @return clean HTML string containing only content elements,
     *         or empty if the page could not be fetched/parsed
     */
    Optional<String> extractContent(String articleUrl);
}
