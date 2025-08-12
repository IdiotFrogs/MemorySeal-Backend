package com.memoryseal.memorysealbackend.global.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneratedToken {
    private String accessToken;
    private String refreshToken;
}
