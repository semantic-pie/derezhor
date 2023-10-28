package io.github.semanticpie.derezhor.externalAgents.exampleAgent.services;

import org.ostis.scmemory.model.element.node.ScNode;

public interface ExampleService {
    ScNode getByName(String name);

    ScNode getByNameWithJmanticService(String name);
}
