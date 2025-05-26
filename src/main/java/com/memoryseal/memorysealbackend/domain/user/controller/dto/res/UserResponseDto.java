package com.memoryseal.memorysealbackend.dto;

import com.memoryseal.memorysealbackend.domain.user.entity.User;
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

    private String profileUrl;

    private String email;

    /*public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.email = user.getEmail();
    }*/
    public static UserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }else {
            return UserResponseDto.builder()
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileUrl())
                    .email(user.getEmail())
                    .build();
        }
    }

}

