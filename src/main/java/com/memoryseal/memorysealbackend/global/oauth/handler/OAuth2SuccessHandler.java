package com.memoryseal.memorysealbackend.oauth.handler;

import com.memoryseal.memorysealbackend.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.jwt.JwtUtil;
import com.memoryseal.memorysealbackend.oauth.data.OAuth2UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String provider = oAuth2User.getAttribute("provider");

        String role = oAuth2User.getAuthorities().stream().
                findFirst()
                .orElseThrow(IllegalAccessError::new)
                .getAuthority();

        GeneratedToken token = jwtUtil.generateToken(email, role);
        log.info("jwtToken = {}", token.getAccessToken());

        String targetUrl = UriComponentsBuilder.fromUriString("/auth/login")
                .queryParam("email", (String) oAuth2User.getAttribute("email"))
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        response.sendRedirect(targetUrl);
    }
}
