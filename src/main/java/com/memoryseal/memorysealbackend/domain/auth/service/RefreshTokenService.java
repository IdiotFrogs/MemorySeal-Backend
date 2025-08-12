package com.memoryseal.memorysealbackend.domain.auth.service;

import com.memoryseal.memorysealbackend.domain.auth.entity.RefreshToken;
import com.memoryseal.memorysealbackend.domain.auth.repository.RefreshTokenRepository;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.global.security.jwt.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
                .orElseThrow(IllegalArgumentException::new);
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public GeneratedToken reissue(String accessToken) {
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByAccessToken(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 토큰 or refresh token을 찾을 수 없음"));

        String storedRefreshToken = refreshTokenEntity.getRefreshToken();
        String userEmail = refreshTokenEntity.getId();

        if(!jwtUtil.verifyToken(storedRefreshToken)) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new IllegalArgumentException("만료되거나 유효하지 않은 토큰");
        }

        String userRole = jwtUtil.getRole(storedRefreshToken);

        GeneratedToken newToken = jwtUtil.generateToken(userEmail, userRole);

        return  newToken;
    }
}
