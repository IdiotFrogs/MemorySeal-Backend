package com.memoryseal.memorysealbackend.domain.auth.controller.dto.res;

import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
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
        description = "로그인 응답 DTO",
        requiredProperties = {"token", "isOnboarding"}
)
public class LoginResponseDto {
    private GeneratedToken token;
    private Boolean isOnboarding;
}
