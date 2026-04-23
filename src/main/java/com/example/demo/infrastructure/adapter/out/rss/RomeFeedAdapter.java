package com.example.demo.infrastructure.adapter.out.rss;

import com.example.demo.application.port.out.RssFeedPort;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Infrastructure adapter — Phase 2: Feed Parsing.
 *
 * <p>Implements {@link RssFeedPort} using the <a href="https://rometools.github.io/rome/">Rome</a>
 * library to parse RSS 0.9x, RSS 1.0, RSS 2.0, and Atom 1.0 feeds.
 *
 * <p>Thumbnail extraction strategy (in order):
 * <ol>
 *   <li>First {@code <enclosure>} tag with {@code type} starting with {@code image/}.</li>
 *   <li>First URL in {@code <media:content>} or {@code <media:thumbnail>} via Rome's
 *       MediaModule (if present).</li>
 *   <li>{@code null} if no image found.</li>
 * </ol>
 */
@Component
public class RomeFeedAdapter implements RssFeedPort {

    private static final Logger log = Logger.getLogger(RomeFeedAdapter.class.getName());

    @Override
    public List<RawFeedEntry> fetchFeed(String feedUrl) {
        List<RawFeedEntry> entries = new ArrayList<>();

        try {
            URL url = URI.create(feedUrl).toURL();
            SyndFeedInput input = new SyndFeedInput();
            input.setAllowDoctypes(true);

            try (XmlReader reader = new XmlReader(url)) {
                SyndFeed feed = input.build(reader);

                for (SyndEntry entry : feed.getEntries()) {
                    entries.add(mapEntry(entry));
                }
            }

            log.info("[FeedParser] feedUrl=" + feedUrl + " entries=" + entries.size());

        } catch (Exception e) {
            log.warning("[FeedParser] Failed to parse feedUrl=" + feedUrl + " → " + e.getMessage());
        }

        return entries;
    }

    // -------------------------------------------------------------------------
    // Mapping helpers
    // -------------------------------------------------------------------------

    private RawFeedEntry mapEntry(SyndEntry entry) {
        String guid        = entry.getUri();
        String title       = entry.getTitle();
        String link        = entry.getLink();
        String description = extractDescription(entry);
        String thumbnail   = extractThumbnail(entry);
        String author      = entry.getAuthor();
        ZonedDateTime publishedAt = toZonedDateTime(entry.getPublishedDate());

        return new RawFeedEntry(guid, title, link, description, thumbnail, author, publishedAt);
    }

    private String extractDescription(SyndEntry entry) {
        if (entry.getDescription() != null) {
            return entry.getDescription().getValue();
        }
        if (entry.getContents() != null && !entry.getContents().isEmpty()) {
            return entry.getContents().get(0).getValue();
        }
        return null;
    }

    /**
     * Try to find a thumbnail image URL from the feed entry.
     * Checks enclosures first, then falls back to media module metadata.
     */
    private String extractThumbnail(SyndEntry entry) {
        // 1. Check enclosures
        if (entry.getEnclosures() != null) {
            for (SyndEnclosure enc : entry.getEnclosures()) {
                if (enc.getType() != null && enc.getType().startsWith("image/")) {
                    return enc.getUrl();
                }
            }
        }

        // 2. Try Rome's MediaModule (media:thumbnail / media:content)
        try {
            com.rometools.modules.mediarss.MediaEntryModule media =
                    (com.rometools.modules.mediarss.MediaEntryModule)
                            entry.getModule(com.rometools.modules.mediarss.MediaModule.URI);
            if (media != null) {
                // Check media:thumbnail in metadata
                if (media.getMetadata() != null && media.getMetadata().getThumbnail() != null && media.getMetadata().getThumbnail().length > 0) {
                    return media.getMetadata().getThumbnail()[0].getUrl().toString();
                }

                // Check media:groups
                if (media.getMediaGroups() != null) {
                    for (com.rometools.modules.mediarss.types.MediaGroup group : media.getMediaGroups()) {
                        if (group.getMetadata() != null && group.getMetadata().getThumbnail() != null && group.getMetadata().getThumbnail().length > 0) {
                            return group.getMetadata().getThumbnail()[0].getUrl().toString();
                        }
                    }
                }

                // Check media:content
                if (media.getMediaContents() != null) {
                    for (com.rometools.modules.mediarss.types.MediaContent content : media.getMediaContents()) {
                        if (content.getReference() != null) {
                            return content.getReference().toString();
                        }
                        if (content.getMetadata() != null && content.getMetadata().getThumbnail() != null && content.getMetadata().getThumbnail().length > 0) {
                            return content.getMetadata().getThumbnail()[0].getUrl().toString();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // MediaModule not present or cast failed — not critical
        }

        return null;
    }

    private ZonedDateTime toZonedDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.of("UTC"));
    }
}
