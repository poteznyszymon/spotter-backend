package com.example.spotter.service;

import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.model.AttachmentEntity;
import com.example.spotter.model.UserEntity;
import com.example.spotter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserService {

    @Value("${app.avatars-bucket-name}")
    private String avatarBucketName;

    @Value("${app.avatars-public-url}")
    private String publicUrl;

    private final S3Service s3Service;
    private final UserRepository userRepository;

    public UserService(S3Service s3Service, UserRepository userRepository) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        validateFile(file);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (user.getAvatar() != null) {
            s3Service.deleteFile(user.getAvatar().getObjectKey(), avatarBucketName);
        }

        String objectKey = s3Service.uploadFile(file, avatarBucketName);

        AttachmentEntity avatar = AttachmentEntity.builder()
                .bucketName(avatarBucketName)
                .objectKey(objectKey)
                .build();

        user.setAvatar(avatar);
        userRepository.save(user);
        return String.format("%s/%s", publicUrl, objectKey);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");
    }
}
