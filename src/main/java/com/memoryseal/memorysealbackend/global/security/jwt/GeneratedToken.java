package com.memoryseal.memorysealbackend.global.security.jwt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(
        description = "로그인 또는 토큰 재발급 성공 시 응답되는 구조",
        requiredProperties = {"accessToken", "refreshToken", "accessTokenExpiresIn"}
)
public class GeneratedToken {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn;
    //private Long refreshTokenExpiresIn;
}
