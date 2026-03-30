package com.memoryseal.memorysealbackend.global.oauth.apple;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AppleTokenResponse {
    private String accessToken;
    private Long expiresIn;
    private String idToken;
    private String refreshToken;
    private String tokenType;
}
