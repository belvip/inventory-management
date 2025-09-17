package com.belvinard.inventory_management.service.impl;

import com.belvinard.inventory_management.exception.MinioOperationException;
import com.belvinard.inventory_management.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.minio.errors.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final String bucketName;
    private final String minioUrl;

    public MinioServiceImpl(MinioClient minioClient,
                            @Value("${minio.bucket-name}") String bucketName,
                            @Value("${minio.url}") String minioUrl) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.minioUrl = minioUrl;
    }

    @PostConstruct
    public void init() {
        try {
            ensureBucketExists();
        } catch (Exception e) {
            log.warn("MinIO initialization failed: {}. MinIO features will be unavailable.", e.getMessage());
        }
    }

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created", bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Bucket initialization failed: {}", e.getMessage());
            throw new MinioOperationException("Bucket initialization failed", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        try {
            validateFile(file);

            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String extension = originalFileName.contains(".") ?
                    originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
            String fileName = UUID.randomUUID() + extension;

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            log.info("File '{}' uploaded", fileName);
            return fileName;

        } catch (Exception e) {
            log.error("Upload failed", e);
            throw new IOException("File upload failed", e);
        }
    }

    @Override
    public String getPreSignedUrl(String objectName, Integer expiryInMinutes) {
        try {
            validateObjectName(objectName);
            int expiry = expiryInMinutes != null ? expiryInMinutes * 60 : 1800; // 30min default

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .expiry(expiry)
                            .build()
            );
        } catch (Exception e) {
            log.error("Pre-signed URL generation failed", e);
            throw new MinioOperationException("URL generation failed", e);
        }
    }

    private void validateFile(MultipartFile file) throws IOException {
        // 1. Basic null and empty checks (you already had this logic)
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        // 2. Check for path traversal attacks and basic filename validity
        if (originalFilename.contains("..") || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid file name: " + originalFilename);
        }

        // 3. Validate File Extension using a robust regex
        String extension = getFileExtension(originalFilename).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");
        if (extension.isEmpty() || !allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException(
                    "Invalid file type. Allowed formats: " + allowedExtensions + ". Received: '" + extension + "'"
            );
        }
    }
    private void validateObjectName(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Object name cannot be null or empty");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}