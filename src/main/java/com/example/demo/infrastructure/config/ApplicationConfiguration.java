package com.example.demo.infrastructure.config;

import com.example.demo.application.port.out.AccessTokenProvider;
import com.example.demo.application.port.out.ArticleContentPort;
import com.example.demo.application.port.out.ArticleRepository;
import com.example.demo.application.port.out.ArticleSourceRepository;
import com.example.demo.application.port.out.CommentRepository;
import com.example.demo.application.port.out.CrawlArticlePort;
import com.example.demo.application.port.out.FeedDiscoveryPort;
import com.example.demo.application.port.out.NewResourceRepository;
import com.example.demo.application.port.out.PasswordHasher;
import com.example.demo.application.port.out.PasswordResetRepository;
import com.example.demo.application.port.out.ReactionRepository;
import com.example.demo.application.port.out.RefreshTokenRepository;
import com.example.demo.application.port.out.RssFeedPort;
import com.example.demo.application.port.out.UserRepository;
import com.example.demo.application.service.ArticleQueryService;
import com.example.demo.application.service.AuthService;
import com.example.demo.application.service.CommentService;
import com.example.demo.application.service.CrawlService;
import com.example.demo.application.service.ProfileService;
import com.example.demo.application.service.ReactionService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    AuthService authService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordResetRepository passwordResetRepository,
            PasswordHasher passwordHasher,
            AccessTokenProvider accessTokenProvider,
            Clock clock) {
        return new AuthService(
                userRepository,
                refreshTokenRepository,
                passwordResetRepository,
                passwordHasher,
                accessTokenProvider,
                clock);
    }

    @Bean
    ProfileService profileService(UserRepository userRepository, Clock clock) {
        return new ProfileService(userRepository, clock);
    }

    @Bean
    ArticleQueryService articleQueryService(
            ArticleRepository articleRepository,
            ArticleSourceRepository articleSourceRepository,
            CommentRepository commentRepository,
            ReactionRepository reactionRepository) {
        return new ArticleQueryService(
                articleRepository,
                articleSourceRepository,
                commentRepository,
                reactionRepository);
    }

    @Bean
    CommentService commentService(
            CommentRepository commentRepository,
            UserRepository userRepository,
            ArticleRepository articleRepository,
            Clock clock) {
        return new CommentService(commentRepository, userRepository, articleRepository, clock);
    }

    @Bean
    ReactionService reactionService(
            ReactionRepository reactionRepository,
            ArticleRepository articleRepository,
            ArticleQueryService articleQueryService,
            Clock clock) {
        return new ReactionService(reactionRepository, articleRepository, articleQueryService, clock);
    }

    @Bean
    CrawlService crawlService(
            NewResourceRepository newResourceRepository,
            FeedDiscoveryPort feedDiscoveryPort,
            RssFeedPort rssFeedPort,
            ArticleContentPort articleContentPort,
            CrawlArticlePort crawlArticlePort,
            Clock clock) {
        return new CrawlService(
                newResourceRepository,
                feedDiscoveryPort,
                rssFeedPort,
                articleContentPort,
                crawlArticlePort,
                clock);
    }
}
