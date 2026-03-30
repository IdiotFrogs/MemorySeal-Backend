package com.memoryseal.memorysealbackend.domain.auth.controller.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleLoginRequest {
    private String idToken;
    private String authorizationCode;
}
