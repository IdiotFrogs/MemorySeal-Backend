package com.memoryseal.memorysealbackend.domain.contributor.service;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.ContentJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.FCM.FCMService;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContentJpaRepository contentJpaRepository;
    private final FCMService fcmService;

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
                    boolean isMe = false;
                    if(user.getId().equals(currentUserId)) {
                        isMe = true;
                    }
                    return ContributorResponseDto.builder()
                            .contributorRole(contributor.getContributorRole())
                            .nickname(user.getNickname())
                            .userId(user.getId())
                            .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                            .userActiveStatus(user.getUserActiveStatus())
                            .isMe(isMe)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TimeCapsuleResponseDto buryCapsule(Long capsuleId, LocalDateTime openedAt) {
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

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(timeCapsule.getTimeCapsuleStatus() != TimeCapsuleStatus.BEFOREBURIED) {
            throw new AuthException(ErrorCode.ALREADY_BURIED);
        }

        Contributor contributor = contributorJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        if(contributor.getContributorRole() != ContributorRole.HOST) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(openedAt.isBefore(LocalDateTime.now())) {
            throw new AuthException(ErrorCode.INVALID_OPENED_AT);
        }

        timeCapsule.setOpenedAt(openedAt);
        timeCapsule.setTimeCapsuleStatus(TimeCapsuleStatus.BURIED);
        timeCapsule.setBuriedAt(LocalDateTime.now());

        List<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId);
        List<Long> userIds = contributors.stream()
                .map(Contributor::getUserId)
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        contributors.forEach(c -> {
            User user = userMap.get(c.getUserId());
            fcmService.sendBuriedNotification(user.getFcmToken(), timeCapsule.getTitle());
        });

        List<TimeCapsuleContent> myContents = contentJpaRepository.findByTimeCapsuleIdAndUserId(capsuleId, currentUserId);

        int myContentCount = (int) myContents.stream()
                .filter(c -> c.getContent() != null && !c.getContent().isBlank())
                .count();

        int myImageCount = (int) myContents.stream()
                .flatMap(c -> c.getAttachedFiles().stream())
                .count();


        return TimeCapsuleResponseDto.builder()
                .title(timeCapsule.getTitle())
                .description(timeCapsule.getDescription())
                .createdAt(timeCapsule.getCreatedAt())
                .openedAt(timeCapsule.getOpenedAt())
                .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                .userRole(contributor.getContributorRole())
                .myContentCount(myContentCount)
                .myImageCount(myImageCount)
                .build();
    }
}
