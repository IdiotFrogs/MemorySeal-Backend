package com.memoryseal.memorysealbackend.domain.user.controller.dto.req;

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
@Schema(description = "사용자 정보 수정 요청 DTO")
public class UserUpdateDto {

    @Schema(description = "변경할 닉네임", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nickname;

    @Schema(description = "변경할 이메일", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;


    public static UserUpdateDto toDto(User user) {
        if (user == null) {
            return null;
        }else {
            return UserUpdateDto.builder()
                    .nickname(user.getNickname())
                    .email(user.getEmail())
                    .build();
        }
    }

}
