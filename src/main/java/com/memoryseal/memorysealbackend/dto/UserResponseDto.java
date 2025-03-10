package com.memoryseal.memorysealbackend.dto;

import com.memoryseal.memorysealbackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private String nickname;

    private String proFileUrl;

    private String email;

    public UserResponseDto(User user) {
    }
}
