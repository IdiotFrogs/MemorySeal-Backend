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
import com.memoryseal.memorysealbackend.global.redis.util.RandomUtil;
import com.memoryseal.memorysealbackend.global.redis.util.RedisUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
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
        timeCapsuleJpaRepository.findById(capsuleId).orElseThrow(() -> new IllegalArgumentException("타임캡슐이 존재하지 않음"));

        final Optional<String> link = redisUtil.getData(INVITE_LINK_PREFIX.formatted(capsuleId), String.class);
        if(link.isEmpty()) {
            final String randomCode = RandomUtil.generateRandomCode('0', 'z', 10);
            redisUtil.setDataExpire(INVITE_LINK_PREFIX.formatted(capsuleId),randomCode,RedisUtil.toTomorrow());
            return new InviteResponseDto(randomCode);
        }
        return new InviteResponseDto(link.get());
    }

    public void submitContributorRequest(final Long capsuleId, final String inviteCode, final Long userId) {
        timeCapsuleJpaRepository.findById(capsuleId).orElseThrow(() -> new IllegalArgumentException("타임캡슐이 존재하지 않음."));
        final Optional<String> storedCode = redisUtil.getData(INVITE_LINK_PREFIX.formatted(capsuleId), String.class);
        if(storedCode.isEmpty() || !storedCode.get().equals(inviteCode)) {
            throw new IllegalArgumentException("유효하지 않은 초대 코드");
        }

        contributorRequestJpaRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
                .ifPresent(req -> {throw new IllegalStateException("이미 공동작업자 요청을 보냄");});
        contributorJpaRepository.findByUserIdAndTimeCapsuleId(userId, capsuleId)
                .ifPresent(req -> {throw new IllegalStateException("이미 공동작업자로 등록됨");});

        final ContributorRequest newRequest = ContributorRequest.builder()
                .userId(userId)
                .timeCapsuleId(capsuleId)
                .build();
        contributorRequestJpaRepository.save(newRequest);
    }

    public void processContributorRequest(final Long requestId, final Long hostId, final boolean isApproved) throws AccessDeniedException {
        ContributorRequest request = contributorRequestJpaRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("요청이 존재하지 않음."));

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(request.getTimeCapsuleId())
                .orElseThrow(() -> new IllegalArgumentException("타임캡슐이 존재하지 않음."));

        if(!timeCapsule.getUserId().equals(hostId)) {
            throw new AccessDeniedException("처리 권한 없음.");
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
