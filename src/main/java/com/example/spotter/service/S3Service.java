package com.example.spotter.service;

import com.example.spotter.exception.exceptions.S3FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${app.s3-public-url}")
    private String publicUrl;

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        String randomUuid = UUID.randomUUID().toString();
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = extension != null ? randomUuid + "." + extension : randomUuid;
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;
        } catch (S3Exception e) {
            throw new RuntimeException("Could not upload file from S3 (AWS Error)", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to upload file from S3 (Connection Error)", e);
        } catch (IOException e) {
            throw new S3FileUploadException("Error while uploading file to S3");
        }
    }

    public void deleteFile(String fileName, String bucketName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Could not delete file from S3 (AWS Error)", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to delete file from S3 (Connection Error)", e);
        }
    }

    public List<String> listBuckets() {
        try {
            return s3Client.listBuckets().buckets()
                    .stream()
                    .map(Bucket::name)
                    .toList();
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to list S3 buckets (AWS Error)", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to list S3 buckets (Connection Error)", e);
        }
    }

    public List<String> listObject(String bucketName) {
        try {
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsV2Request);
            return response.contents()
                    .stream()
                    .map(S3Object::key)
                    .toList();
        } catch (S3Exception e) {
            throw new RuntimeException("Could not list all buckets from S3 (AWS Error)", e);
        } catch (SdkClientException e) {
            throw new RuntimeException("Failed to list all buckets from from S3 (Connection Error)", e);
        }
    }

    public String getPublicUrl(String bucketName) {
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalArgumentException("Bucket name cannot be null or empty");
        }
        if (!publicUrl.contains("{bucket}")) {
            throw new IllegalStateException("Application configuration error: Invalid S3 Public URL format");
        }
        return publicUrl.replace("{bucket}", bucketName);
    }

}
