package com.example.demo.infrastructure.adapter.out.rss;

import com.example.demo.application.port.out.ArticleContentPort;
import net.dankito.readability4j.Article;
import net.dankito.readability4j.Readability4J;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Infrastructure adapter — Phase 5: Content Extraction.
 *
 * <p>Implements {@link ArticleContentPort} using Readability4J (a port of Mozilla's Readability.js)
 * to accurately extract the main content of an article while filtering out boilerplate.
 */
@Component
public class JsoupContentExtractorAdapter implements ArticleContentPort {

    private static final Logger log = Logger.getLogger(JsoupContentExtractorAdapter.class.getName());

    private static final int TIMEOUT_MS = 15_000;
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

    /** Jsoup Safelist: only keeps useful content tags, strips everything else. */
    private static final Safelist CONTENT_SAFELIST = Safelist.none()
            .addTags("p", "br", "b", "strong", "i", "em",
                    "ul", "ol", "li",
                    "h1", "h2", "h3", "h4", "h5", "h6",
                    "blockquote", "img", "figure", "figcaption")
            .addAttributes("img", "src", "alt", "title", "width", "height")
            .addAttributes("a", "href");

    @Override
    public Optional<String> extractContent(String articleUrl) {
        try {
            // 1. Fetch the page with Jsoup
            Document doc = Jsoup.connect(articleUrl)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .followRedirects(true)
                    .get();

            // 2. Use Readability4J to identify and extract the main article content
            Readability4J readability4J = new Readability4J(articleUrl, doc.html());
            Article article = readability4J.parse();

            String contentHtml = article.getContent();
            if (contentHtml == null || contentHtml.isBlank()) {
                log.fine("[ContentExtractor] Readability failed to extract content for: " + articleUrl);
                return Optional.empty();
            }

            // 3. Clean the HTML using our Safelist to ensure consistent output format
            String cleanHtml = Jsoup.clean(contentHtml, articleUrl, CONTENT_SAFELIST);
            if (cleanHtml.isBlank()) {
                return Optional.empty();
            }

            return Optional.of(cleanHtml);

        } catch (IOException e) {
            log.warning("[ContentExtractor] Failed to fetch url=" + articleUrl + " → " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.severe("[ContentExtractor] Error processing " + articleUrl + ": " + e.getMessage());
            return Optional.empty();
        }
    }
}
