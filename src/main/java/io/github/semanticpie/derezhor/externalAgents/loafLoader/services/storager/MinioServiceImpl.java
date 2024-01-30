package io.github.semanticpie.derezhor.externalAgents.loafLoader.services.storager;

import io.github.semanticpie.derezhor.externalAgents.loafLoader.models.Resource;
import io.github.semanticpie.derezhor.externalAgents.loafLoader.services.sync.ResourceAlreadyExistException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

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
    public Resource getFile(String hash) {
        try {
            GetObjectResponse object = minio.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(hash)
                            .build()
            );
            return new Resource(object, MediaType.valueOf(Objects.requireNonNull(object.headers().get("Content-Type"))));
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    @Override
    public void putFile(String hash, MultipartFile multipartFile) throws ResourceAlreadyExistException {
        try {

            InputStream inputStream = multipartFile.getInputStream();
            if (!isObjectExist(hash)) {
                minio.putObject(PutObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(hash)
                        .contentType(multipartFile.getContentType())
                        .stream(inputStream, inputStream.available(), -1).build());
            } else {
                throw new ResourceAlreadyExistException("Failed to upload file to MinIO");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }


    @Override
    public Iterable<Result<Item>> getAllFiles() {
        return minio.listObjects(ListObjectsArgs.builder().bucket(BUCKET_NAME).build());
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
