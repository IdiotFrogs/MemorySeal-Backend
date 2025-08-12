package com.memoryseal.memorysealbackend.global.oauth.handler;

import com.memoryseal.memorysealbackend.domain.auth.service.RefreshTokenService;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.global.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    /*
    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;
    */

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        String role = oAuth2User.getAuthorities().stream().
                findFirst()
                .orElseThrow(() -> new IllegalStateException("역할을 찾을 수 없음"))
                .getAuthority();

        GeneratedToken token = jwtUtil.generateToken(email, role);
        log.info("generate accessToken = {}", token.getAccessToken());
        log.info("generate refreshToken = {}", token.getRefreshToken());

        refreshTokenService.saveTokenInfo(email, token.getRefreshToken(), token.getAccessToken());

        String targetUrl = UriComponentsBuilder.fromUriString("/auth/login")
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
