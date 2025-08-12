package com.memoryseal.memorysealbackend.domain.auth.service;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public GeneratedToken execute(String email) {
        GeneratedToken generatedToken = jwtUtil.generateToken(email, Role.USER.getKey());

        refreshTokenService.saveTokenInfo(email, generatedToken.getRefreshToken(), generatedToken.getAccessToken());

        return generatedToken;
    }
}
