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
public class MyUserResponseDto {

    private String nickname;

    private String profileUrl;

    private String email;


    public static MyUserResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }else {
            return MyUserResponseDto.builder()
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileUrl())
                    .email(user.getEmail())
                    .build();
        }
    }
}
