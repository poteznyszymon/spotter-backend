package com.example.spotter.mapper;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.service.S3Service;
import com.example.spotter.utils.enums.ModelConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Value("${app.avatars-bucket-name}")
    private String avatarsBucketName;

    private final ModelConverter modelConverter;
    private final S3Service s3Service;

    public UserMapper(ModelConverter modelConverter, S3Service s3Service) {
        this.modelConverter = modelConverter;
        this.s3Service = s3Service;
    }

    public UserDTO toResponse(UserEntity user) {
        String avatarUrl = null;
        UserDTO dto = modelConverter.convert(user, UserDTO.class);

        if (user.getAvatar() != null) {
            avatarUrl = String.format("%s/%s", s3Service.getPublicUrl(avatarsBucketName), user.getAvatar().getObjectKey());
        }

        dto.getAvatar().setUrl(avatarUrl);
        return dto;
    }

}
