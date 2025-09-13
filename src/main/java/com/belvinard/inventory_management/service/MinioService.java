package com.belvinard.inventory_management.service;

import com.belvinard.inventory_management.exception.MinioOperationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MinioService {
    String uploadImage(MultipartFile file) throws IOException, MinioOperationException;
    String getPreSignedUrl(String objectName, Integer expiryInMinutes) throws MinioOperationException;
    void deleteFile(String objectName) throws MinioOperationException;
    InputStream downloadFile(String objectName) throws MinioOperationException;
    List<String> listFiles() throws MinioOperationException;
    boolean fileExists(String objectName) throws MinioOperationException;
    String getFileUrl(String objectName) throws MinioOperationException;
}