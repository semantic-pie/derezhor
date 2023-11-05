package io.github.semanticpie.derezhor.common.services;

import io.github.semanticpie.derezhor.config.ReopenTask;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Timer;

@Slf4j
@Service
public class KeepAliveService {

    @Value("${connection-timeout:2000}")
    private Integer pingTime;
    private final Timer timer = new Timer();
    private final DefaultScContext context;

    @Autowired
    public KeepAliveService(DefaultScContext context) {
        this.context = context;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        this.timer.scheduleAtFixedRate(new ReopenTask(context), pingTime, pingTime);
    }
}
