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

        if(!StringUtils.hasText(atc)) {
            doFilter(request, response, filterChain);
            return;
        }

        String token = null;
        if(atc.startsWith("Bearer ")) {
            token = atc.substring("Bearer ".length());
        }else {
            filterChain.doFilter(request, response);
            return;
        }

        if(!jwtUtil.verifyToken(token)) {
            System.out.println("실패");
            throw new JwtException("Access Token 만료");
        }

        if(jwtUtil.verifyToken(token)) {
            User findUser = userJpaRepository.findByEmail(jwtUtil.getUid(token))
                    .orElseThrow(IllegalStateException::new);

            Authentication auth = getAuthentication(findUser);
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("adasd");
        }

        filterChain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
