package com.example.demo.infrastructure.scheduler;

import com.example.demo.application.port.in.CrawlUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Scheduled trigger for the RSS crawl pipeline.
 *
 * <p>Runs automatically every hour. Can also be triggered manually via the
 * admin API endpoint if needed.
 *
 * <p>The cron expression {@code "0 0 * * * *"} means: at second 0, minute 0,
 * of every hour, every day.
 */
@Component
public class CrawlScheduler {

    private static final Logger log = Logger.getLogger(CrawlScheduler.class.getName());

    private final CrawlUseCase crawlUseCase;

    public CrawlScheduler(CrawlUseCase crawlUseCase) {
        this.crawlUseCase = crawlUseCase;
    }

    /**
     * Trigger crawl. 
     * initialDelay = 10000: Chạy lần đầu sau 10 giây khi server start.
     * fixedDelay = 600000: Sau đó cứ mỗi 30 phút chạy một lần.
     */
    @Scheduled(initialDelay = 10000, fixedDelay = 600000*3)
    public void scheduledCrawl() {
        log.info("[CrawlScheduler] Scheduled crawl triggered");
        try {
            crawlUseCase.crawlAllSources();
            log.info("[CrawlScheduler] Scheduled crawl completed successfully");
        } catch (Exception e) {
            log.severe("[CrawlScheduler] Scheduled crawl failed: " + e.getMessage());
        }
    }
}
