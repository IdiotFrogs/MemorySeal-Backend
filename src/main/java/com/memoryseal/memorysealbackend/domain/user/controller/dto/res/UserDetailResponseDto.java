package com.memoryseal.memorysealbackend.domain.user.controller.dto.res;

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
        description = "사용자 프로필 조회 응답 DTO",
        requiredProperties = {"nickname", "profileImageUrl", "email", "isOnboarding"}
)
public class UserDetailResponseDto {
    private Long id;

    private String nickname;

    private String profileImageUrl;

    private String email;

    private Boolean isOnboarding;

    /*public UserResponseDto(User user) {
        this.nickname = user.getNickname();
        this.profileUrl = user.getProfileUrl();
        this.email = user.getEmail();
    }*/
    public static UserDetailResponseDto toDto(User user) {
        if (user == null) {
            return null;
        }else {
            String imageUrl = null;
            if(user.getProfileImage() != null) {
                imageUrl = user.getProfileImage().getFileUrl();
            }
            return UserDetailResponseDto.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .profileImageUrl(imageUrl)
                    .email(user.getEmail())
                    .isOnboarding(user.getIsOnboarding())
                    .build();
        }
    }

}
