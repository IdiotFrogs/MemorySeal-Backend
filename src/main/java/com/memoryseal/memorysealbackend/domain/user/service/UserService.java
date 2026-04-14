package com.memoryseal.memorysealbackend.domain.user.service;

import com.memoryseal.memorysealbackend.domain.auth.repository.RefreshTokenRepository;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.repository.AttachedFileJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserCreateDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserDetailResponseDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.res.UserResponseDto;
import com.memoryseal.memorysealbackend.domain.user.controller.dto.req.UserUpdateDto;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.memoryseal.memorysealbackend.global.oauth.apple.AppleAuthClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final S3Service s3Service;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AppleAuthClient appleAuthClient;

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

    public User createUser(UserCreateDto userCreateDTO) {
        User user = User.builder()
                .nickname(userCreateDTO.getNickname())
                .build();
        return userJpaRepository.save(user);
    }

    @Transactional
    public UserResponseDto signUpUser(String nickname, MultipartFile profileImage) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof User principalUser)) {
            throw new AuthException(ErrorCode.NEED_LOGIN);
        }

        User user = userJpaRepository.findById(principalUser.getId())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if(user.getIsOnboarding()) {
            throw new AuthException(ErrorCode.ALREADY_ONBOARDED);
        }

        if(userJpaRepository.existsByNickname(nickname)) {
            throw new AuthException(ErrorCode.DUPLICATE_NICKNAME);
        }

        if(profileImage != null && !profileImage.isEmpty()) {
            s3Service.uploadProfileImage(profileImage, user.getId());
        }

        user.setNickname(nickname);

        user.setIsOnboarding(true);

        return UserResponseDto.toDto(user);
    }


    public UserResponseDto getDetail(Long id) {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new AuthException(ErrorCode.USER_NOT_FOUND)
        );
        return UserResponseDto.toDto(user);
    }

    public UserDetailResponseDto getMyDetail() {
        Long currentUserId = getCurrentUserId();
        User user = userJpaRepository.findById(currentUserId).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        return UserDetailResponseDto.toDto(user);
    }

    @Transactional
    public UserResponseDto updateMyDetail(String nickname, MultipartFile file) throws IOException {
        Long currentUserId = getCurrentUserId();
        User user = userJpaRepository.findById(currentUserId).orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
        if(nickname != null && !nickname.isBlank()) {
            if(userJpaRepository.existsByNicknameAndIdNot(nickname, currentUserId)) {
                throw new AuthException(ErrorCode.DUPLICATE_NICKNAME);
            }
            user.setNickname(nickname);
        }

        if(file != null && !file.isEmpty()) {
            s3Service.uploadProfileImage(file, currentUserId);
        }

        User updateUser = userJpaRepository.save(user);

        return UserResponseDto.toDto(updateUser);
    }



    @Transactional
    public UserResponseDto updateUser(Long id, String nickname, MultipartFile file) throws IOException {
        User user = userJpaRepository.findById(id).orElseThrow(
                () -> new AuthException(ErrorCode.USER_NOT_FOUND)
        );
        if(nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        }

        if(file != null && !file.isEmpty()) {
            s3Service.uploadProfileImage(file, id);
        }

        User updateUser = userJpaRepository.save(user);

        return UserResponseDto.toDto(updateUser);
    }

    @Transactional
    public void withdrawUser() {
        Long currentUserId = getCurrentUserId();

        User user = userJpaRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if("apple".equals(user.getProvider()) && user.getAppleRefreshToken() != null) {
            try {
                appleAuthClient.revokeAppleToken(user.getAppleRefreshToken());
                log.info("Apple 연동 해제 완료: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Apple 연동 해제 실패: {}", e.getMessage());
            }
        }

        String mask = "_withdrawn_" + UUID.randomUUID().toString().substring(0, 8);

        user.setUserActiveStatus(false);

        user.setEmail(user.getEmail() + mask);
        if(user.getProviderId() != null) {
            user.setProviderId(user.getProviderId() + mask);
        }

        user.setNickname("탈퇴한 사용자");
        user.setProfileImage(null);
        user.setAppleRefreshToken(null);

        refreshTokenRepository.deleteById(user.getEmail());

        log.info("유저 탈퇴 처리 완료: ID = {}, Email = {}", currentUserId, user.getEmail());
    }
}
