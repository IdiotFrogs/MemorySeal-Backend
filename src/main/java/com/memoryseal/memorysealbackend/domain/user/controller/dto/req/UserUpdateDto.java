package com.memoryseal.memorysealbackend.domain.user.controller.dto.req;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {

    private String nickname;

    private AttachedFile profileUrl;

    private String email;


    public static UserUpdateDto toDto(User user) {
        if (user == null) {
            return null;
        }else {
            return UserUpdateDto.builder()
                    .nickname(user.getNickname())
                    .profileUrl(user.getProfileImage())
                    .email(user.getEmail())
                    .build();
        }
    }

}
