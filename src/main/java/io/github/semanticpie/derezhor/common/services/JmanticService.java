package io.github.semanticpie.derezhor.common.services;

import lombok.Getter;
import org.ostis.api.context.DefaultScContext;

@Getter
public class JmanticService {
    private final DefaultScContext context;

    public JmanticService(DefaultScContext context){
        this.context = context;
    }
}
