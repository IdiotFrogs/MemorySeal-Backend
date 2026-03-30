package com.memoryseal.memorysealbackend.domain.user.controller.dto.res;

import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        description = "사용자 정보 수정 응답 DTO",
        requiredProperties = {"nickname", "profileImageUrl", "email"}
)
public class UserResponseDto {
    private Long id;

    private String nickname;

    private String profileImageUrl;

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
            String imageUrl = null;
            if(user.getProfileImage() != null) {
                imageUrl = user.getProfileImage().getFileUrl();
            }
            return UserResponseDto.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(imageUrl)
                    .email(user.getEmail())
                    .build();
        }
    }

}

