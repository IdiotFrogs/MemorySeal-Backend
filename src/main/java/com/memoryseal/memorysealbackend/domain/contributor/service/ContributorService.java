package com.memoryseal.memorysealbackend.domain.contributor.service;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;

    public List<ContributorResponseDto> getDetail(Long capsuleId) {
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
        if(!timeCapsuleJpaRepository.existsById(capsuleId)) {
            throw new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND);
        }
        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(capsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }
        List<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId);

        return contributors.stream()
                .map(contributor -> {
                    User user = userJpaRepository.findById(contributor.getUserId())
                            .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
                    return ContributorResponseDto.builder()
                            .userId(user.getId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                            .userActiveStatus(user.getUserActiveStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
