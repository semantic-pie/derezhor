package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.sync;

import io.github.semanticpie.derezhor.externalAgents.loafLoader.services.knowledger.KnowledgeService;
import io.github.semanticpie.derezhor.externalAgents.loafLoader.services.storager.StorageService;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ostis.scmemory.model.exception.ScMemoryException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@AllArgsConstructor
public class SyncResourcesServiceImpl implements SyncResourcesService{
    private final KnowledgeService knowledgeService;
    private final StorageService storageService;

    @Override
    public String sync(MultipartFile multipartFile)  {
        String hash = null;
        try {

            log.info("START [upload][file][hash]");
            long hashTime = System.currentTimeMillis();
            InputStream inputStream = multipartFile.getInputStream();
            hash = DigestUtils.md5DigestAsHex(inputStream);
            inputStream.close();
            log.info("FINISH [upload][file][has] [{}]", System.currentTimeMillis() - hashTime);

            log.info("START [upload][file][minio]");
            long minioTime = System.currentTimeMillis();
            inputStream = multipartFile.getInputStream();
            syncWithStorage(hash, inputStream);
            inputStream.close();
            log.info("FINISH [upload][file][minio] [{}]", System.currentTimeMillis() - minioTime);

            log.info("FINISH [upload][file][ostis]");
            long scTime = System.currentTimeMillis();
            syncWithKnowladges(hash, multipartFile);
            log.info("FINISH [upload][file][ostis] [{}]", System.currentTimeMillis() - scTime);

            return hash;
        } catch (ResourceAlreadyExistException e) {
            return hash;
        } catch (IOException | MinioException | ScMemoryException e) {
            throw new SyncException(e);
        }
    }

    private void syncWithStorage(String hash, InputStream inputStream) throws IOException, MinioException {
        storageService.putFile(hash, inputStream);
    }

    private void syncWithKnowladges(String hash, MultipartFile multipartFile) throws ScMemoryException {
        knowledgeService.putFile(hash, multipartFile.getContentType());
    }

    @Override
    public InputStream resourceInputStream(String hash) {
        try {
            return storageService.getFile(hash);
        } catch (RuntimeException e) {
            throw new SyncException("Can't load this resource", e);
        }
    }
}
