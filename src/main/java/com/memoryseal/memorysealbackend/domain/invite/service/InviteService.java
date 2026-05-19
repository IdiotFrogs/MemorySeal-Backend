package com.memoryseal.memorysealbackend.domain.invite.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.FCM.FCMService;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.memoryseal.memorysealbackend.global.redis.util.RandomUtil;
import com.memoryseal.memorysealbackend.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {
    private final RedisUtil redisUtil;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FCMService fcmService;

    private static final String INVITE_LINK_PREFIX = "id=%d";



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


    public InviteResponseDto generateInviteCode(final long capsuleId) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(timeCapsule.getTimeCapsuleStatus() != TimeCapsuleStatus.BEFOREBURIED) {
            throw new AuthException(ErrorCode.ALREADY_BURIED);
        }

        final Optional<String> existingCode = redisUtil.getData(INVITE_LINK_PREFIX.formatted(capsuleId),String.class);
        if(existingCode.isPresent()) {
            return new InviteResponseDto(existingCode.get());
        }

        final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
        redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(capsuleId),randomCode,RedisUtil.toTomorrow());
        // 역방향키 추가
        redisUtil.setDataExpire("code=" + randomCode, String.valueOf(capsuleId), RedisUtil.toTomorrow());
        return new InviteResponseDto(randomCode);
    }

    @Transactional
    public void submitContributorRequest(final String inviteCode) {
        Long currentUserId = getCurrentUserId();

        Long capsuleId = redisUtil.getData("code=" + inviteCode, Long.class)
                        .orElseThrow(() -> new AuthException(ErrorCode.INVALID_INVITE_CODE));

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));
        if(timeCapsule.getTimeCapsuleStatus() != TimeCapsuleStatus.BEFOREBURIED) {
            throw new AuthException(ErrorCode.ALREADY_BURIED);
        }

        contributorJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                        .ifPresent(req -> {throw new AuthException(ErrorCode.ALREADY_CONTRIBUTOR);});

        Contributor newContributor = Contributor.builder()
                .userId(currentUserId)
                .timeCapsuleId(capsuleId)
                .contributorRole(ContributorRole.CONTRIBUTOR)
                .build();
        contributorJpaRepository.save(newContributor);

        User joinUser = userJpaRepository.findById(currentUserId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        List<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId);
        List<Long> userIds = contributors.stream()
                .map(Contributor::getUserId)
                .filter(id -> !id.equals(currentUserId))
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u-> u));

        userMap.values().forEach(user ->
                fcmService.sendJoinRequestNotification(user.getFcmToken(), timeCapsule.getTitle(), joinUser.getNickname()));
    }
}
