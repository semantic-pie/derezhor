package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.storager;

import io.github.semanticpie.derezhor.externalAgents.loafLoader.services.sync.ResourceAlreadyExistException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class MinioServiceImpl implements StorageService {

    @Value("${minio.bucket}")
    public String BUCKET_NAME;
    private final MinioClient minio;

    @Autowired
    public MinioServiceImpl(MinioClient minio) {
        this.minio = minio;
    }

    @Override
    public InputStream getFile(String hash) {
        try {
            return minio.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(hash)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    @Override
    public void putFile(String hash, InputStream inputStream) throws ResourceAlreadyExistException {
        try {
            if (!isObjectExist(hash)) {
                minio.putObject(PutObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(hash)
                        .stream(inputStream, inputStream.available(), -1).build());
            } else {
                throw new ResourceAlreadyExistException("Failed to upload file to MinIO");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public boolean isObjectExist(String name) {
        try {
            minio.statObject(StatObjectArgs.builder()
                    .bucket(BUCKET_NAME)
                    .object(name).build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
