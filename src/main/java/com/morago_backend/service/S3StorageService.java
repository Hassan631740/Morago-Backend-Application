package com.morago_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
@Profile({"prod", "production"})
public class S3StorageService implements StorageService {

    private final S3Client s3Client;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${storage.s3.base-url:https://%s.s3.amazonaws.com}")
    private String baseUrlTemplate;

    public S3StorageService(
            @Value("${storage.s3.region}") String region,
            @Value("${storage.s3.access-key}") String accessKey,
            @Value("${storage.s3.secret-key}") String secretKey,
            @Value("${storage.s3.endpoint:}") String endpoint
    ) {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)));
        if (endpoint != null && !endpoint.isEmpty()) {
            builder = builder.endpointOverride(URI.create(endpoint));
        }
        this.s3Client = builder.build();
    }

    @Override
    public String upload(String folder, String filename, MultipartFile file) {
        try {
            String key = folder + "/" + UUID.randomUUID() + "-" + filename;
            PutObjectRequest put = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(put, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            String base = String.format(baseUrlTemplate, bucket);
            return base + "/" + key;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload to S3", e);
        }
    }

    @Override
    public void delete(String path) {
        if (path == null) return;
        // assume path after bucket URL is the key
        int idx = path.indexOf(bucket);
        if (idx < 0) return;
        String key = path.substring(idx + bucket.length() + 1);
        DeleteObjectRequest del = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        s3Client.deleteObject(del);
    }

    @Override
    public Resource download(String path) {
        try {
            String key = extractKeyFromPath(path);
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
            byte[] content = s3Object.readAllBytes();
            return new ByteArrayResource(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to download from S3", e);
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            String key = extractKeyFromPath(path);
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String extractKeyFromPath(String path) {
        if (path == null) throw new IllegalArgumentException("Path cannot be null");
        int idx = path.indexOf(bucket);
        if (idx < 0) {
            // If bucket not found in path, assume path is the key
            return path.startsWith("/") ? path.substring(1) : path;
        }
        return path.substring(idx + bucket.length() + 1);
    }
}


