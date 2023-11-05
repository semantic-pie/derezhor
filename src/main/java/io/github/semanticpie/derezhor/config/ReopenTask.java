package io.github.semanticpie.derezhor.config;

import lombok.extern.slf4j.Slf4j;
import org.ostis.api.context.DefaultScContext;

import java.util.TimerTask;

@Slf4j
public class ReopenTask extends TimerTask {
    private final DefaultScContext context;
    public ReopenTask(DefaultScContext context) {
        this.context = context;
    }


    @Override
    public void run() {
        try {
            boolean isOpen = false;

            try {
                isOpen = context.memory().isOpen();
            } catch (NullPointerException ignored) {}

            if (!isOpen) {
                context.memory().open();
            }

        } catch (Exception ignored) {}
    }
}
