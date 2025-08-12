package com.memoryseal.memorysealbackend.domain.auth.controller;

import com.memoryseal.memorysealbackend.domain.auth.service.RefreshTokenService;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.domain.auth.service.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/login/google")
    public void loginGoogle(HttpServletResponse response) throws IOException {
        String googleLoginUrl = "/oauth2/authorization/google";
        response.sendRedirect(googleLoginUrl);
    }

    @GetMapping("/login/apple")
    public void loginApple(HttpServletResponse response) throws IOException {
        String appleLoginUrl = "/oauth2/authorization/apple";
        response.sendRedirect(appleLoginUrl);
    }

    @PostMapping("/reissue")
    public ResponseEntity<GeneratedToken> reissueAccessToken(@RequestHeader("Authorization") String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("authorization header가 잘못: {}", authorizationHeader);

            return ResponseEntity.badRequest().build();
        }
        String accessToken = authorizationHeader.substring(7);

        try{
            GeneratedToken newToken = refreshTokenService.reissue(accessToken);
            return ResponseEntity.ok(newToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authroization") String authorizationHeader) {
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authorizationHeader.substring(7);

        try {
            refreshTokenService.removeRefreshToken(accessToken);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
