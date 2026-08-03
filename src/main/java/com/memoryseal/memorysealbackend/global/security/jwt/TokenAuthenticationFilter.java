package com.memoryseal.memorysealbackend.global.security.jwt;

import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserJpaRepository userJpaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String atc = request.getHeader("Authorization");

        if(!StringUtils.hasText(atc) || !atc.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = atc.substring(7);

        try {
            if(jwtUtil.verifyToken(token)) {
                String email = jwtUtil.getUid(token);
                String provider = jwtUtil.getProvider(token);
                User findUser = userJpaRepository.findByEmailAndProvider(email, provider)
                        .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
                Authentication auth = getAuthentication(findUser);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }catch (Exception e) {
            log.error("Token authentication failed: {}", e.getMessage());
            throw new JwtException("유효하지 않은 토큰입니다.");
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }

    @Override
    public boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth/login") || path.equals("/auth/reissue");
    }
}
