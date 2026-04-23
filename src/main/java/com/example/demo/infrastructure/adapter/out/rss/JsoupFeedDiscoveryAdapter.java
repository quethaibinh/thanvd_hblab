package com.example.demo.infrastructure.adapter.out.rss;

import com.example.demo.application.port.out.FeedDiscoveryPort;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Infrastructure adapter — Phase 1: Feed Discovery.
 *
 * <p>Implements {@link FeedDiscoveryPort} using Jsoup to scrape a news source's
 * RSS overview page (e.g. {@code https://vnexpress.net/rss}) and discover
 * the URLs of all individual RSS/Atom feed files.
 *
 * <p>Detection strategy (in order):
 * <ol>
 *   <li>HTML {@code <link>} tags with {@code type="application/rss+xml"} or
 *       {@code type="application/atom+xml"} — standard auto-discovery.</li>
 *   <li>Anchor {@code <a href="...">} tags whose href ends with
 *       {@code .rss}, {@code .atom}, or {@code /feed}.</li>
 *   <li>If nothing is discovered from the page, fall back to returning the
 *       page URL itself (treating it as a direct feed URL).</li>
 * </ol>
 */
@Component
public class JsoupFeedDiscoveryAdapter implements FeedDiscoveryPort {

    private static final Logger log = Logger.getLogger(JsoupFeedDiscoveryAdapter.class.getName());

    /** Jsoup connection timeout in milliseconds. */
    private static final int TIMEOUT_MS = 10_000;

    /** User-Agent to avoid being blocked by news sites. */
    private static final String USER_AGENT =
            "Mozilla/5.0 (compatible; NewsCrawlerBot/1.0; +https://demo.local/bot)";

    @Override
    public List<String> discoverFeedUrls(String rssPageUrl) {
        List<String> feedUrls = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(rssPageUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();

            // Strategy 1: <link> auto-discovery tags
            Elements linkTags = doc.select(
                    "link[type=application/rss+xml], link[type=application/atom+xml]");
            for (Element link : linkTags) {
                String href = link.attr("abs:href");
                if (!href.isBlank()) {
                    feedUrls.add(href);
                }
            }

            // Strategy 2: <a href> anchors pointing to feed files
            if (feedUrls.isEmpty()) {
                Elements anchorTags = doc.select("a[href]");
                for (Element anchor : anchorTags) {
                    String href = anchor.attr("abs:href");
                    if (isFeedUrl(href)) {
                        feedUrls.add(href);
                    }
                }
            }

            log.info("[FeedDiscovery] url=" + rssPageUrl + " discovered=" + feedUrls.size() + " feeds");

        } catch (IOException e) {
            log.warning("[FeedDiscovery] Failed to scrape url=" + rssPageUrl + " → " + e.getMessage());
        }

        // Fallback: treat the URL itself as a direct feed
        if (feedUrls.isEmpty()) {
            log.info("[FeedDiscovery] No feeds found on page, using rssPageUrl as direct feed: " + rssPageUrl);
            feedUrls.add(rssPageUrl);
        }

        return feedUrls;
    }

    /** Returns true if the URL looks like a direct RSS/Atom feed file. */
    private boolean isFeedUrl(String url) {
        if (url == null || url.isBlank()) return false;
        String lower = url.toLowerCase();
        return lower.endsWith(".rss")
                || lower.endsWith(".atom")
                || lower.endsWith("/feed")
                || lower.endsWith("/feed/")
                || lower.contains("/rss/")
                || lower.contains("feed=rss");
    }
}
