package com.example.spotter.service;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.mapper.UserMapper;
import com.example.spotter.model.AttachmentEntity;
import com.example.spotter.model.UserEntity;
import com.example.spotter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UserService {

    @Value("${app.avatars-bucket-name}")
    private String avatarsBucketName;

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(S3Service s3Service, UserRepository userRepository, UserMapper userMapper) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO getUser(Long userId) {
        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + userId + " not found"));
        return userMapper.toResponse(user);
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        validateFile(file);

        UserEntity user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + userId + " not found"));

        if (user.getAvatar() != null) {
            s3Service.deleteFile(user.getAvatar().getObjectKey(), avatarsBucketName);
        }

        String objectKey = s3Service.uploadFile(file, avatarsBucketName);

        AttachmentEntity avatar = AttachmentEntity.builder()
                .bucketName(avatarsBucketName)
                .objectKey(objectKey)
                .originalName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .build();

        user.setAvatar(avatar);
        userRepository.save(user);
        return String.format("%s/%s", s3Service.getPublicUrl(avatarsBucketName), objectKey);
    }

//        public addUsersToOffice()

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");
    }
}
