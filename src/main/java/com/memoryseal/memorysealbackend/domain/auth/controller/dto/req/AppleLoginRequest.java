package com.memoryseal.memorysealbackend.domain.auth.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "애플로그인 시 요청 DTO", requiredProperties = {"idToken", "authorizationCode"})
public class AppleLoginRequest {
    @Schema(description = "idToken")
    private String idToken;

    @Schema(description = "authorizationCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorizationCode;
}
