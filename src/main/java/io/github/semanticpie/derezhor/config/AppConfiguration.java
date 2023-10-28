package io.github.semanticpie.derezhor.config;

import io.github.semanticpie.derezhor.common.services.CacheService;
import io.github.semanticpie.derezhor.common.services.JmanticService;
import io.github.semanticpie.derezhor.common.services.KeepAliveService;
import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;
import org.ostis.scmemory.model.ScMemory;
import org.ostis.scmemory.websocketmemory.memory.SyncOstisScMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.net.URI;

@Slf4j
@Configuration
@ComponentScan("io.github.semanticpie.derezhor")
public class AppConfiguration {

    @Value("${derezhor.sc-machine.url}")
    private String scMachineURL;

    @Bean
    protected DefaultScContext contextBean() throws Exception {
        ScMemory memory = new SyncOstisScMemory(new URI(scMachineURL));
        return new DefaultScContext(memory);
    }

    @Bean
    public JmanticService jmanticServiceBean() throws Exception {
        return new JmanticService(contextBean());
    }

    @Autowired
    ApplicationContext context;
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() throws Exception {
        log.info("doSomethingAfterStartup");
        KeepAliveService keepAliveService = new KeepAliveService(context.getBean(CacheService.class), contextBean());
        log.info("try listen:");
        keepAliveService.listen();
    }

}
