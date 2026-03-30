package com.memoryseal.memorysealbackend.domain.invite.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequest;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRequestStatus;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorRequestJpaRepository;
import com.memoryseal.memorysealbackend.domain.invite.controller.dto.res.InviteResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import com.memoryseal.memorysealbackend.global.redis.util.RandomUtil;
import com.memoryseal.memorysealbackend.global.redis.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {
    private final RedisUtil redisUtil;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
    private final ContributorRequestJpaRepository contributorRequestJpaRepository;

    private static final String INVITE_LINK_PREFIX = "id=%d";

    public InviteResponseDto generateInviteCode(final long capsuleId) {
        timeCapsuleJpaRepository.findById(capsuleId).orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(capsuleId), String.class);
        if(link.isEmpty()) {
            final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(capsuleId),randomCode,RedisUtil.toTomorrow());
            return new InviteResponseDto(randomCode);
        }
        return new InviteResponseDto(link.get());
    }

    public void submitContributorRequest(final Long capsuleId, final String inviteCode, final Long userId) {
        timeCapsuleJpaRepository.findById(capsuleId).orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));
        final Optional<String> storedCode = redisUtil.getData(INVITE_LINK_PREFIX.formatted(capsuleId), String.class);
        if(storedCode.isEmpty() || !storedCode.get().equals(inviteCode)) {
            throw new AuthException(ErrorCode.INVALID_INVITE_CODE);
        }

        contributorRequestJpaRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
                .ifPresent(req -> {throw new AuthException(ErrorCode.ALREADY_REQUESTED);});
        contributorJpaRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
                .ifPresent(req -> {throw new AuthException(ErrorCode.ALREADY_CONTRIBUTOR);});

        final ContributorRequest newRequest = ContributorRequest.builder()
                .userId(userId)
                .timeCapsuleId(capsuleId)
                .build();
        contributorRequestJpaRepository.save(newRequest);
    }

    public void processContributorRequest(final Long requestId, final Long hostId, final boolean isApproved) {
        ContributorRequest request = contributorRequestJpaRepository.findById(requestId)
                .orElseThrow(() -> new AuthException(ErrorCode.REQUEST_NOT_FOUND));

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(request.getTimeCapsuleId())
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(!timeCapsule.getUserId().equals(hostId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(isApproved) {
            final Contributor newContributor = Contributor.builder()
                    .userId(request.getUserId())
                    .timeCapsuleId(request.getTimeCapsuleId())
                    .contributorRole(ContributorRole.CONTRIBUTOR)
                    .build();
            contributorJpaRepository.save(newContributor);
            request.updateStatus(ContributorRequestStatus.APPROVED);
        }else {
            request.updateStatus(ContributorRequestStatus.REJECTED);
        }
        contributorRequestJpaRepository.save(request);
    }
}
