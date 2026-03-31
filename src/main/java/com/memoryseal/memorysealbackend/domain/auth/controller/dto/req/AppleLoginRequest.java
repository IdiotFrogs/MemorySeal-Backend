package com.memoryseal.memorysealbackend.domain.auth.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleLoginRequest {
    private String idToken;
    @Schema(description = "authorizationCode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorizationCode;
}
