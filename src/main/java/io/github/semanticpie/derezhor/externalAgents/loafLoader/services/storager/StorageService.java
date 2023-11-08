package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.storager;


import java.io.InputStream;

public interface StorageService {
    InputStream getFile(String hash);
    void putFile(String hash, InputStream inputStream);
}
