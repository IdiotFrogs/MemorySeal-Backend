package com.memoryseal.memorysealbackend.service;

import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import com.memoryseal.memorysealbackend.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final JwtUtil jwtUtil;

    public GeneratedToken execute(String email) {
        return jwtUtil.generateToken(email, Role.USER.getKey());
    }
}
