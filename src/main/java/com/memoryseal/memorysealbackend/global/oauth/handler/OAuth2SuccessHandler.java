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


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        log.info("OAuth2 attributes: {}", oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");

        String role = oAuth2User.getAuthorities().stream().
                findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .orElse("ROLE_USER");

        GeneratedToken token = jwtUtil.generateToken(email, role, provider);
        log.info("generate accessToken = {}", token.getAccessToken());
        log.info("generate refreshToken = {}", token.getRefreshToken());

        refreshTokenService.saveTokenInfo(email, token.getRefreshToken(), token.getAccessToken());

        String targetUrl = UriComponentsBuilder.fromUriString("http://43.201.236.253.sslip.io:8080/auth/login/success")
                //http://localhost:3000/oauth/callback
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
