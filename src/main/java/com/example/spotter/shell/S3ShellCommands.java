package com.example.spotter.shell;

import com.example.spotter.service.S3Service;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@ShellComponent
public class S3ShellCommands {

    private final S3Service s3Service;

    public S3ShellCommands(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @ShellMethod("List all buckets")
    public List<String> listAllBuckets() {
        return s3Service.listBuckets();
    }

    @ShellMethod("List all object")
    public List<String> listAllObjects(String bucketName) {
        return s3Service.listObject(bucketName);
    }

    @ShellMethod("Delete object from bucket")
    public void deleteObjectFromBucket(String bucketName, String fileName) {
        s3Service.deleteFile(fileName, bucketName);
    }
}
