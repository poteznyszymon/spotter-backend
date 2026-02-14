package com.example.spotter.mapper;

import com.example.spotter.dto.UserDTO;
import com.example.spotter.model.UserEntity;
import com.example.spotter.utils.enums.ModelConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Value("${app.avatars-public-url}")
    private String publicUrl;

    private final ModelConverter modelConverter;

    public UserMapper(ModelConverter modelConverter) {
        this.modelConverter = modelConverter;
    }

    public UserDTO toResponse(UserEntity user) {
        String avatarUrl = null;
        UserDTO dto = modelConverter.convert(user, UserDTO.class);

        if (user.getAvatar() != null) {
            avatarUrl = String.format("%s/%s", publicUrl, user.getAvatar().getObjectKey());
        }

        dto.setAvatarUrl(avatarUrl);
        return dto;
    }

}
