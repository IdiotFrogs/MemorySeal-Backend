package com.memoryseal.memorysealbackend.domain.invite.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequestStatus;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorRequestJpaRepository;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.ContributorRequestResDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteSubmitResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
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
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {
    private final RedisUtil redisUtil;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
    private final ContributorRequestJpaRepository contributorRequestJpaRepository;
    private final UserJpaRepository userJpaRepository;

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

        final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
        redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(capsuleId),randomCode,RedisUtil.toTomorrow());
        // 역방향키 추가
        redisUtil.setDataExpire("code=" + randomCode, String.valueOf(capsuleId), RedisUtil.toTomorrow());
        return new InviteResponseDto(randomCode);
    }

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

        Optional<ContributorRequest> existingRequest = contributorRequestJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, capsuleId);

        if(existingRequest.isPresent()) {
            ContributorRequest request = existingRequest.get();
            if(request.getStatus() == ContributorRequestStatus.REJECTED) {
                request.setStatus(ContributorRequestStatus.PENDING);
                contributorRequestJpaRepository.save(request);
                return ;
            }
            throw new AuthException(ErrorCode.ALREADY_REQUESTED);
        }

        final ContributorRequest newRequest = ContributorRequest.builder()
                .userId(currentUserId)
                .timeCapsuleId(capsuleId)
                .build();
        contributorRequestJpaRepository.save(newRequest);
    }

    public void processContributorRequest(final Long requestId, final boolean isApproved) {
        Long currentUserId = getCurrentUserId();
        ContributorRequest request = contributorRequestJpaRepository.findById(requestId)
                .orElseThrow(() -> new AuthException(ErrorCode.REQUEST_NOT_FOUND));

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(request.getTimeCapsuleId())
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(!timeCapsule.getUserId().equals(currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }
        if(request.getStatus() != ContributorRequestStatus.PENDING) {
            throw new AuthException(ErrorCode.ALREADY_PROCESSED);
        }

        if(isApproved) {
            final Contributor newContributor = Contributor.builder()
                    .userId(request.getUserId())
                    .timeCapsuleId(request.getTimeCapsuleId())
                    .contributorRole(ContributorRole.CONTRIBUTOR)
                    .build();
            contributorJpaRepository.save(newContributor);
            request.setStatus(ContributorRequestStatus.APPROVED);
        }else {
            request.setStatus(ContributorRequestStatus.REJECTED);
        }
        contributorRequestJpaRepository.save(request);
    }

    public List<ContributorRequestResDto> getReqeustsDetail(Long capsuleId) {
        Long currentUserId = getCurrentUserId();

        timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        Contributor contributor = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        if(contributor.getContributorRole() != ContributorRole.HOST) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        List<ContributorRequest> requests = contributorRequestJpaRepository.findByTimeCapsuleId(capsuleId);

        return requests.stream()
                .map(r -> {
                    User user = userJpaRepository.findById(r.getUserId())
                            .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
                    return ContributorRequestResDto.toDto(r, user);
                })
                .toList();
    }
}
