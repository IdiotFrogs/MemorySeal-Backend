package com.memoryseal.memorysealbackend.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.memoryseal.memorysealbackend.domain.auth.entity.Role;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.entity.FileType;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.memoryseal.memorysealbackend.global.oauth.apple.AppleAuthClient;
import com.memoryseal.memorysealbackend.global.oauth.apple.AppleProperties;
import com.memoryseal.memorysealbackend.global.oauth.apple.AppleTokenResponse;
import com.memoryseal.memorysealbackend.global.security.config.GoogleProperties;
import com.memoryseal.memorysealbackend.global.security.jwt.GeneratedToken;
import com.memoryseal.memorysealbackend.global.security.jwt.JwtUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserJpaRepository userJpaRepository;
    private final GoogleProperties googleProperties;
    private final AppleAuthClient appleAuthClient;

    @Value("${app.default-image-url}")
    private String defaultProfileUrl;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        Object principal = authentication.getPrincipal();
        Long currentUserId;
        if(principal instanceof User) {
            currentUserId = ((User) principal).getId();
        }else if(principal instanceof String) {
            currentUserId = Long.valueOf((String) principal);
        }else {
            log.error("예상치 못한 Principal 타입: {}", principal.getClass().getName());
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }
        return currentUserId;
    }

    @Transactional
    public String verifyGoogleIdToken(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(googleProperties.getIds())
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if(idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String providerId = payload.getSubject();

                if(email == null) {
                    throw new AuthException(ErrorCode.USER_NOT_FOUND);
                }

                if(!userJpaRepository.existsByProviderAndProviderIdAndUserActiveStatus("google", providerId, true)) {
                    String name = (String) payload.get("name");
                    if(name == null || name.isEmpty()) {
                        name = "GoogleUser";
                    }
                    AttachedFile defaultProfile = AttachedFile.builder()
                            .fileUrl(defaultProfileUrl)
                            .fileSize(0L)
                            .fileType(FileType.IMAGE)
                            .isMain(false)
                            .build();

                    User newUser = User.builder()
                            .email(email)
                            .nickname(name)
                            .provider("google")
                            .providerId(providerId)
                            .userActiveStatus(true)
                            .role(Role.USER)
                            .isOnboarding(false)
                            .profileImage(defaultProfile)
                            .build();
                    userJpaRepository.save(newUser);
                    log.info("Google 신규 회원가입 완료: {}", email);
                }
                return providerId;
            }else {
                throw new AuthException(ErrorCode.INVALID_PARAMETER);
            }
        }catch (AuthException e){
            throw e;
        }catch (HttpClientErrorException.Unauthorized e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }catch (Exception e) {
            log.error("Google Token 검증 오류: {}", e.getMessage());
            throw new AuthException(ErrorCode.GOOGLE_SERVER_ERROR);
        }
    }

    @Transactional
    public String verifyAppleIdToken(String idTokenString, String authorizationCode) {
        log.info("받은 idToken: {}", idTokenString);
        log.info("받은 authCode: {}", authorizationCode);
        try {
            JWTClaimsSet claims = appleAuthClient.verifyIdToken(idTokenString);
            String email = claims.getStringClaim("email");
            String providerId = claims.getSubject();


            if(!userJpaRepository.existsByProviderAndProviderIdAndUserActiveStatus("apple", providerId, true)) {
                if (email == null) {
                    throw new AuthException(ErrorCode.USER_NOT_FOUND);
                }
                String appleRefreshToken = null;
                boolean hasCode = (authorizationCode != null);
                if(hasCode) {
                    AppleTokenResponse appleTokenResponse = appleAuthClient.getAppleToken(authorizationCode);
                    appleRefreshToken = appleTokenResponse.getRefreshToken();
                }


                AttachedFile defaultProfile = AttachedFile.builder()
                        .fileUrl(defaultProfileUrl)
                        .fileSize(0L)
                        .fileType(FileType.IMAGE)
                        .isMain(false)
                        .build();

                User newUser = User.builder()
                        .email(email)
                        .nickname("AppleUser")
                        .provider("apple")
                        .providerId(providerId)
                        .appleRefreshToken(appleRefreshToken)
                        .userActiveStatus(true)
                        .role(Role.USER)
                        .isOnboarding(false)
                        .profileImage(defaultProfile)
                        .build();

                userJpaRepository.save(newUser);
                log.info("Apple 신규 회원가입 완료: {}", email);
            }
            return providerId;
        }catch (AuthException e){
            throw e;
        }catch (HttpClientErrorException.Unauthorized e) {
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }catch (Exception e) {
            log.error("Apple Token 검증 오류: {}", e.getMessage());
            throw new AuthException(ErrorCode.APPLE_SERVER_ERROR);
        }
    }

    @Transactional
    public void updateFcmToken(String fcmToken) {
        if(fcmToken == null || fcmToken.isBlank()) {
            throw new AuthException(ErrorCode.INVALID_FCM_TOKEN);
        }

        Long currentUserId = getCurrentUserId();
        User user = userJpaRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
        user.setFcmToken(fcmToken);
    }


    public GeneratedToken execute(String providerId, String provider) {
        User user = userJpaRepository.findByProviderAndProviderIdAndUserActiveStatus(provider, providerId, true)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        GeneratedToken generatedToken = jwtUtil.generateToken(user.getEmail(), Role.USER.getKey());

        refreshTokenService.saveTokenInfo(user.getEmail(), generatedToken.getRefreshToken(), generatedToken.getAccessToken());

        return generatedToken;
    }
}
