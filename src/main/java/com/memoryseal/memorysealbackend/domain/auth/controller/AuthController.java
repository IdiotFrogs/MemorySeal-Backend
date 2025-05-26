package com.memoryseal.memorysealbackend.controller;

import com.memoryseal.memorysealbackend.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.jwt.JwtUtil;
import com.memoryseal.memorysealbackend.repository.RefreshTokenRepository;
import com.memoryseal.memorysealbackend.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginService loginService;

    @GetMapping("/login")
    public ResponseEntity<GeneratedToken> login(@RequestParam String email) {
        return ResponseEntity.ok(loginService.execute(email));
    }

}
