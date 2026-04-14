package com.memoryseal.memorysealbackend.domain.auth.controller.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "구글로그인 시 요청 DTO", requiredProperties = {"idToken"})
public class GoogleLoginRequest {
    @Schema(description = "idToken")
    private String idToken;
}
