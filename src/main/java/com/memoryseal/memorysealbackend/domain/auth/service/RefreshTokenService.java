package com.memoryseal.memorysealbackend.domain.auth.service;

import com.memoryseal.memorysealbackend.domain.auth.entity.RefreshToken;
import com.memoryseal.memorysealbackend.domain.auth.repository.RefreshTokenRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.global.security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void saveTokenInfo(String email, String refreshToken, String accessToken) {
        refreshTokenRepository.findById(email)
                .ifPresentOrElse(
                        token -> {
                            token.updateAccessToken(accessToken);
                            token.setRefreshToken(refreshToken);
                            refreshTokenRepository.save(token);
                        },
                        () -> {
                            refreshTokenRepository.save(new RefreshToken(email, accessToken, refreshToken));
                        }
                );
    }

    @Transactional
    public void removeRefreshToken(String accessToken) {
        RefreshToken token = refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new AuthException(ErrorCode.REFRESHTOKEN_NOT_FOUND));
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public GeneratedToken reissue(String refreshToken) {
        String actualToken = refreshToken;
        if(StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
            actualToken = refreshToken.substring(7);
        }
        if(!jwtUtil.verifyToken(actualToken)) {
            throw new AuthException(ErrorCode.EXPIRED_TOKEN);
        }

        String userEmail = jwtUtil.getUid(actualToken);

        RefreshToken tokenEntity = refreshTokenRepository.findById(userEmail)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if(!tokenEntity.getRefreshToken().equals(actualToken)) {
            refreshTokenRepository.delete(tokenEntity);
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        String userRole = jwtUtil.getRole(actualToken);
        GeneratedToken newToken = jwtUtil.generateToken(userEmail, userRole);

        tokenEntity.updateAccessToken(newToken.getAccessToken());
        tokenEntity.setRefreshToken(newToken.getRefreshToken());
        refreshTokenRepository.save(tokenEntity);

        return newToken;
    }
}
