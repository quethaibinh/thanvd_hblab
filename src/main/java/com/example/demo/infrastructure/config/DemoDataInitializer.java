package com.example.demo.infrastructure.config;

import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.application.port.out.ArticleSourceRepository;
import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.application.port.out.PasswordHasher;
import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.application.port.out.UserRepository;
import com.example.demo.domain.model.Article;
import com.example.demo.domain.model.ArticleReaction;
import com.example.demo.domain.model.ArticleSource;
import com.example.demo.domain.model.ArticleSourceType;
import com.example.demo.domain.model.ArticleStatus;
import com.example.demo.domain.model.Comment;
import com.example.demo.domain.model.ReactionType;
import com.example.demo.domain.model.UserAccount;
import com.example.demo.domain.model.UserRole;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArticleSourceRepository articleSourceRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public DemoDataInitializer(
            UserRepository userRepository,
            ArticleSourceRepository articleSourceRepository,
            ArticleRepository articleRepository,
            CommentRepository commentRepository,
            ReactionRepository reactionRepository,
            PasswordHasher passwordHasher,
            Clock clock) {
        this.userRepository = userRepository;
        this.articleSourceRepository = articleSourceRepository;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.reactionRepository = reactionRepository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail("alice@demo.com")) {
            return;
        }

        Instant now = Instant.now(clock);

        UserAccount alice = new UserAccount(
                "user-alice",
                "alice@demo.com",
                passwordHasher.hash("123456"),
                "Alice Nguyen",
                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400",
                "Tech lover and news curator.",
                UserRole.READER,
                true,
                now.minus(20, ChronoUnit.DAYS),
                now.minus(1, ChronoUnit.DAYS));
        UserAccount bob = new UserAccount(
                "user-bob",
                "bob@demo.com",
                passwordHasher.hash("123456"),
                "Bob Tran",
                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400",
                "Product manager who reads everything before breakfast.",
                UserRole.READER,
                true,
                now.minus(18, ChronoUnit.DAYS),
                now.minus(2, ChronoUnit.DAYS));
        userRepository.save(alice);
        userRepository.save(bob);

        ArticleSource hblabSource = new ArticleSource(
                "source-hblab",
                "HBLab Newsroom",
                "hblab-newsroom",
                ArticleSourceType.MANUAL,
                "https://news.demo.local/hblab",
                "https://news.demo.local/hblab/rss.xml",
                "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=400",
                true,
                now.minus(40, ChronoUnit.DAYS),
                Map.of("language", "vi", "country", "VN"));
        ArticleSource globalFeed = new ArticleSource(
                "source-global-wire",
                "Global Wire",
                "global-wire",
                ArticleSourceType.RSS,
                "https://news.demo.local/global-wire",
                "https://news.demo.local/global-wire/rss.xml",
                "https://images.unsplash.com/photo-1520607162513-77705c0f0d4a?w=400",
                true,
                now.minus(30, ChronoUnit.DAYS),
                Map.of("language", "en", "country", "US"));
        articleSourceRepository.saveAll(List.of(hblabSource, globalFeed));

        Article articleOne = new Article(
                "article-ai-agents",
                "rss-001",
                "ai-agents-changing-editorial-workflow",
                "AI agents are changing how editorial teams ship news faster",
                "Many newsrooms are testing AI agents to summarize, tag, and route stories before editors publish.",
                """
                Editorial workflows are shifting from manual triage toward assisted pipelines. In this demo project,
                article content is still fake, but the model is designed so future crawlers can map RSS fields into the
                same domain object without rewriting the application layer.

                A future crawler can populate externalId, sourceArticleUrl, canonicalUrl, crawledAt, sourceId, and
                metadata fields directly. Use cases that list articles or show details will not need to know whether the
                article was manually inserted or crawled from RSS.
                """,
                "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=1200",
                "Technology",
                List.of("AI", "Editorial", "Automation"),
                "Linh Pham",
                hblabSource.id(),
                "https://news.demo.local/hblab/ai-agents-changing-editorial-workflow",
                "https://news.demo.local/articles/ai-agents-changing-editorial-workflow",
                now.minus(6, ChronoUnit.HOURS),
                now.minus(5, ChronoUnit.HOURS),
                ArticleStatus.PUBLISHED,
                Map.of("origin", "manual-seed", "readingTimeMinutes", "4"));
        Article articleTwo = new Article(
                "article-rss-design",
                "rss-002",
                "designing-a-news-domain-for-rss-ingestion",
                "Designing a news domain that survives the jump from fake data to RSS ingestion",
                "A good article model keeps source metadata separate enough that swapping to crawled content is mostly an adapter problem.",
                """
                When a project starts with fake data, the biggest risk is shaping models around the seed instead of the
                real source. This sample keeps an explicit ArticleSource aggregate and stores article metadata in a way
                that later RSS adapters can extend with feed-specific attributes.

                In practice, crawlers can convert RSS items into the Article model and store raw feed values inside the
                metadata map if a business rule does not yet require a dedicated field.
                """,
                "https://images.unsplash.com/photo-1495020689067-958852a7765e?w=1200",
                "Architecture",
                List.of("Clean Architecture", "RSS", "Design"),
                "Minh Le",
                globalFeed.id(),
                "https://news.demo.local/global-wire/designing-a-news-domain-for-rss-ingestion",
                "https://news.demo.local/articles/designing-a-news-domain-for-rss-ingestion",
                now.minus(1, ChronoUnit.DAYS),
                now.minus(20, ChronoUnit.HOURS),
                ArticleStatus.PUBLISHED,
                Map.of("origin", "rss-ready-seed", "readingTimeMinutes", "6"));
        Article articleThree = new Article(
                "article-productivity",
                "rss-003",
                "how-readers-build-a-daily-news-routine",
                "How readers build a healthy daily news routine",
                "From bookmarks to reaction history, product choices shape whether readers come back every morning.",
                """
                Engagement is not only about page views. Reactions, comments, and profile preferences together form the
                basis for recommendation and personalization features later on. This demo keeps them as separate models
                so future ranking or recommendation logic can evolve in the application layer.
                """,
                "https://images.unsplash.com/photo-1504711331083-9c895941bf81?w=1200",
                "Lifestyle",
                List.of("Habits", "Readers", "Product"),
                "Thao Vu",
                hblabSource.id(),
                "https://news.demo.local/hblab/how-readers-build-a-daily-news-routine",
                "https://news.demo.local/articles/how-readers-build-a-daily-news-routine",
                now.minus(2, ChronoUnit.DAYS),
                now.minus(2, ChronoUnit.DAYS),
                ArticleStatus.PUBLISHED,
                Map.of("origin", "manual-seed", "readingTimeMinutes", "3"));
        articleRepository.saveAll(List.of(articleOne, articleTwo, articleThree));

        commentRepository.save(new Comment(
                "comment-1",
                articleOne.id(),
                alice.id(),
                alice.fullName(),
                "Bai viet nay mo ta kha ro cach tach domain va adapter.",
                now.minus(4, ChronoUnit.HOURS)));
        commentRepository.save(new Comment(
                "comment-2",
                articleOne.id(),
                bob.id(),
                bob.fullName(),
                "Minh rat thich y tuong giu externalId de sau nay crawl RSS de hon.",
                now.minus(3, ChronoUnit.HOURS)));
        commentRepository.save(new Comment(
                "comment-3",
                articleTwo.id(),
                alice.id(),
                alice.fullName(),
                "Neu sau nay them ranking thi reaction summary nay se rat huu ich.",
                now.minus(10, ChronoUnit.HOURS)));

        reactionRepository.save(new ArticleReaction("reaction-1", articleOne.id(), alice.id(), ReactionType.LOVE, now.minus(5, ChronoUnit.HOURS)));
        reactionRepository.save(new ArticleReaction("reaction-2", articleOne.id(), bob.id(), ReactionType.LIKE, now.minus(4, ChronoUnit.HOURS)));
        reactionRepository.save(new ArticleReaction("reaction-3", articleTwo.id(), alice.id(), ReactionType.WOW, now.minus(14, ChronoUnit.HOURS)));
    }
}
