package com.example.spotter.service;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.event.UserInvitation;
import com.example.spotter.event.UsersInvitedEvent;
import com.example.spotter.exception.exceptions.UserNotFoundException;
import com.example.spotter.mapper.UserMapper;
import com.example.spotter.model.AttachmentEntity;
import com.example.spotter.model.UserEntity;
import com.example.spotter.repository.UserRepository;
import com.example.spotter.utils.enums.Role;
import com.example.spotter.utils.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;


@Service
@Slf4j
public class UserService {

    @Value("${app.avatars-bucket-name}")
    private String avatarsBucketName;

    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VerificationTokenService verificationTokenService;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(
            S3Service s3Service,
            UserRepository userRepository,
            UserMapper userMapper,
            VerificationTokenService verificationTokenService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.s3Service = s3Service;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.verificationTokenService = verificationTokenService;
        this.eventPublisher = eventPublisher;
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

    @Transactional
    public void inviteEmployeesAndNotify(Long adminId, List<String> emails) {

        UserEntity admin = userRepository
                .findById(adminId)
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + adminId + " not found"));

        List<String> existingEmails = userRepository.findExistingEmails(emails);

        List<String> emailsToInvite = emails.stream()
                .filter(email -> !existingEmails.contains(email))
                .distinct()
                .toList();

        if (emailsToInvite.isEmpty()) return;

        List<UserEntity> usersToSave = emailsToInvite.stream()
                .map(email -> UserEntity.builder()
                        .email(email)
                        .role(Role.EMPLOYEE)
                        .office(admin.getOffice())
                        .enabled(false)
                        .build())
                .toList();

        List<UserEntity> savedUsers = userRepository.saveAll(usersToSave);
        List<UserInvitation> payload = verificationTokenService.createTokens(savedUsers, TokenType.ACTIVATION);
        eventPublisher.publishEvent(UsersInvitedEvent.builder().invitations(payload).build());
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File cannot be empty");
    }
}
