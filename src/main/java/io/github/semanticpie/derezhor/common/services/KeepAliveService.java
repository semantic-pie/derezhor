package io.github.semanticpie.derezhor.common.services;

import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class KeepAliveService {

    private final CacheService cacheService;

    private final DefaultScContext context;

    public void listen() {
        bootstrap();
    }

    public KeepAliveService(CacheService cacheService, DefaultScContext context) {
        this.cacheService = cacheService;
        this.context = context;
    }

    private void bootstrap() {
        while (true) {
            try {
                Thread.sleep(Duration.ofSeconds(1).toMillis());
                context.memory().open();
                if(context.memory().isOpen()){
                    log.info("Connection restored!");
                    cacheService.reconnect();
                }
                do {
                    Thread.sleep(Duration.ofSeconds(1).toMillis());
                } while (context.memory().isOpen());
            } catch (Exception ignored) {
                log.warn("Connection lost, try reconnect...");
            }
        }
    }
}
