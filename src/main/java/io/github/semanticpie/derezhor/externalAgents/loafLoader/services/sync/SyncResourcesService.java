package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.sync;

import io.github.semanticpie.derezhor.externalAgents.loafLoader.models.Resource;
import org.springframework.web.multipart.MultipartFile;


public interface SyncResourcesService {
    String sync(MultipartFile multipartFile);

    void sync();
    Resource getResource(String hash);
}
