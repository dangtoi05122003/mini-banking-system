package com.minibank.UserService.Service;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;
    @Value("${minio.url}")
    private String minioUrl;
    public String uploadFile(String objectName, InputStream inputStream, String contentType, long size) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return minioUrl + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("Upload thất bại: " + e.getMessage());
        }
    }
}
