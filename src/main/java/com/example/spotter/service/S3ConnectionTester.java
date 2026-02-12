package com.example.spotter.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@Component
@Slf4j
public class S3ConnectionTester implements CommandLineRunner {

    private final S3Client s3Client;

    @Value("${app.avatars-bucket-name}")
    private String bucketName;

    public S3ConnectionTester(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void run(String... args) {
        try {
            ListBucketsResponse response = s3Client.listBuckets();
            log.info("Połączono pomyślnie! Znalezione buckety:");
            response.buckets().forEach(b -> log.info(" -> Bucket: {}", b.name()));

            boolean exists = response.buckets().stream()
                    .anyMatch(b -> b.name().equals(bucketName));

            if (exists) {
                log.info("Sukces: Bucket '{}' jest gotowy do pracy.", bucketName);
            } else {
                log.error("Błąd: Nie znaleziono bucketa '{}'!", bucketName);
            }

        } catch (Exception e) {
            log.error("BŁĄD POŁĄCZENIA Z GARAGE: {}", e.getMessage());
        }
    }
}