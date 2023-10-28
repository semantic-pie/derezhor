package io.github.semanticpie.derezhor.externalAgents.exampleAgent.services.impl;

import io.github.semanticpie.derezhor.common.services.CacheService;
import io.github.semanticpie.derezhor.common.services.JmanticService;
import io.github.semanticpie.derezhor.externalAgents.exampleAgent.services.ExampleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostis.scmemory.model.element.node.ScNode;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ExampleServiceImpl implements ExampleService {
    private CacheService service;
    private final JmanticService jmanticService;
    @Override
    public ScNode getByName(String name) {
       return service.get(name);
    }

    @Override
    public ScNode getByNameWithJmanticService(String name) {
        ScNode node = null;
        try {
            node = jmanticService.getContext().findKeynode(name).orElse(null);
        }catch (ScMemoryException e){
            log.warn(e.getLocalizedMessage());
        }
        return node;
    }
}