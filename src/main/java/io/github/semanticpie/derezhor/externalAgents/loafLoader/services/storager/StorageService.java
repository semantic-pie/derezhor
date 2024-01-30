package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.storager;


import io.github.semanticpie.derezhor.externalAgents.loafLoader.models.Resource;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    Resource getFile(String hash);

    void putFile(String hash, MultipartFile multipartFile);

    Iterable<Result<Item>> getAllFiles();
}
