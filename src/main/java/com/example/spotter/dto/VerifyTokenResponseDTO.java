package com.example.spotter.dto;

import com.example.spotter.model.UserEntity;
import com.example.spotter.model.VerificationTokenEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyTokenResponseDTO {
    UserEntity userEntity;
    VerificationTokenEntity tokenEntity;
}
