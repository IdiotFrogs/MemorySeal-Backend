package com.memoryseal.memorysealbackend.config;

import com.memoryseal.memorysealbackend.jwt.TokenAuthenticationFilter;
import com.memoryseal.memorysealbackend.jwt.TokenExceptionFilter;
import com.memoryseal.memorysealbackend.oauth.handler.OAuth2FailureHandler;
import com.memoryseal.memorysealbackend.oauth.handler.OAuth2SuccessHandler;
import com.memoryseal.memorysealbackend.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oauth2FailureHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "favicon.ico");
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)    // csrf disable
                .formLogin(AbstractHttpConfigurer::disable)    // From 로그인 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)    // http basic 인증 방식 disable
                .cors(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/auth/login").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().permitAll());

        // 세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        // jwt 관련 설정
        http
                .addFilterBefore(tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass());

        // oauth2 설정
        http
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(c -> c.userService(customOauth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oauth2FailureHandler));


        return http.build();
    }
}
