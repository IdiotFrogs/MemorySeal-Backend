package com.memoryseal.memorysealbackend.domain.auth.service;

import com.memoryseal.memorysealbackend.domain.auth.entity.RefreshToken;
import com.memoryseal.memorysealbackend.domain.auth.repository.RefreshTokenRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
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
    private final UserJpaRepository userJpaRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void saveTokenInfo(Long userId, String refreshToken, String accessToken) {
        String key = String.valueOf(userId);
        refreshTokenRepository.findById(key)
                .ifPresentOrElse(
                        token -> {
                            token.updateAccessToken(accessToken);
                            token.setRefreshToken(refreshToken);
                            refreshTokenRepository.save(token);
                        },
                        () -> {
                            refreshTokenRepository.save(new RefreshToken(key, accessToken, refreshToken));
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
        String userProvider = jwtUtil.getProvider(actualToken);

        if(userProvider == null) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        User user = userJpaRepository.findByEmailAndProvider(userEmail, userProvider)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        String key = String.valueOf(user.getId());
        RefreshToken tokenEntity = refreshTokenRepository.findById(key)
                .orElseThrow(() -> new AuthException(ErrorCode.REFRESHTOKEN_NOT_FOUND));

        if(!tokenEntity.getRefreshToken().equals(actualToken)) {
            refreshTokenRepository.delete(tokenEntity);
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }

        String userRole = jwtUtil.getRole(actualToken);
        GeneratedToken newToken = jwtUtil.generateToken(userEmail, userRole, userProvider);

        tokenEntity.updateAccessToken(newToken.getAccessToken());
        tokenEntity.setRefreshToken(newToken.getRefreshToken());
        refreshTokenRepository.save(tokenEntity);

        return newToken;
    }
}
