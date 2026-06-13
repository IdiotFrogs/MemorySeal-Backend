package com.memoryseal.memorysealbackend.domain.contributor.service;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.ContributorResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.file.entity.AttachedFile;
import com.memoryseal.memorysealbackend.domain.file.repository.AttachedFileJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleContent;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.ContentJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.FCM.FCMService;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final AttachedFileJpaRepository attachedFileJpaRepository;
    private final S3Service s3Service;

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

    public Page<ContributorResponseDto> getDetail(Long capsuleId, Pageable pageable) {
        Long currentUserId = getCurrentUserId();
        if(!timeCapsuleJpaRepository.existsById(capsuleId)) {
            throw new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND);
        }
        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(capsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }
        Page<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId, pageable);

        List<Long> userIds = contributors.getContent().stream()
                .map(Contributor::getUserId)
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return contributors.map(contributor -> {
            User user = userMap.get(contributor.getUserId());
            return ContributorResponseDto.builder()
                    .contributorRole(contributor.getContributorRole())
                    .nickname(user.getNickname())
                    .userId(user.getId())
                    .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                    .userActiveStatus(user.getUserActiveStatus())
                    .isMe(user.getId().equals(currentUserId))
                    .build();
        });
    }

    @Transactional
    public TimeCapsuleResponseDto buryCapsule(Long capsuleId, LocalDateTime openedAt) {
        Long currentUserId = getCurrentUserId();

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
                .filter(id -> !id.equals(currentUserId))
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        userMap.values().forEach(user ->
                fcmService.sendBuriedNotification(user.getFcmToken(), timeCapsule.getTitle()));

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

    @Transactional
    public void kickContributor(Long capsuleId, Long targetUserId) {
        Long currentUserId = getCurrentUserId();

        Contributor contributor = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        if(contributor.getContributorRole() != ContributorRole.HOST) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        Contributor targetContributor = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(targetUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));
        if(targetContributor.getContributorRole() == ContributorRole.HOST) {
            throw new AuthException(ErrorCode.CANNOT_KICK_HOST);
        }

        contributorJpaRepository.delete(targetContributor);
    }

    @Transactional
    public void leaveTimeCapsule(Long capsuleId) {
        Long currentUserId = getCurrentUserId();
        Contributor contributor = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        if(contributor.getContributorRole() == ContributorRole.HOST) {
            throw new AuthException(ErrorCode.HOST_CANNOT_LEAVE);
        }

        List<TimeCapsuleContent> myContents = contentJpaRepository.findByTimeCapsuleIdAndUserId(capsuleId, currentUserId);

        if(!myContents.isEmpty()) {
            List<Long> contentIds = myContents.stream()
                    .map(TimeCapsuleContent::getId)
                    .toList();

            List<AttachedFile> files = attachedFileJpaRepository.findByTimeCapsuleContentIdIn(contentIds);
            if(!files.isEmpty()) {
                files.parallelStream()
                        .forEach(file -> s3Service.deleteFileFromS3(file.getFileUrl()));
                List<Long> fileIds = files.stream()
                        .map(AttachedFile::getId)
                        .toList();
                attachedFileJpaRepository.deleteAllByIdInBatch(fileIds);
            }
            contentJpaRepository.deleteAllByIdInBatch(contentIds);
        }

        contributorJpaRepository.delete(contributor);
    }

    @Transactional
    public void delegationHost(Long capsuleId, Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        if(currentUserId.equals(targetUserId)) {
            throw new AuthException(ErrorCode.CANNOT_DELEGATE_TO_SELF);
        }

        Contributor host = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));

        if(host.getContributorRole() != ContributorRole.HOST) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        Contributor targetContributor = contributorJpaRepository
                .findByUserIdAndTimeCapsuleId(targetUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.NOT_A_CONTRIBUTOR));

        host.setContributorRole(ContributorRole.CONTRIBUTOR);
        targetContributor.setContributorRole(ContributorRole.HOST);

        contributorJpaRepository.save(host);
        contributorJpaRepository.save(targetContributor);
    }

    public Page<ContributorResponseDto> searchByNickname(Long capsuleId, String nickname, Pageable pageable) {
        Long currentUserId = getCurrentUserId();

        if(!timeCapsuleJpaRepository.existsById(capsuleId)) {
            throw new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND);
        }

        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(capsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(nickname == null || nickname.isBlank()) {
            return getDetail(capsuleId, pageable);
        }

        List<Long> userIds = userJpaRepository.findByNicknameContaining(nickname.trim()).stream()
                .map(User::getId)
                .toList();

        if(userIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Contributor> contributors = contributorJpaRepository
                .findByTimeCapsuleIdAndUserIdIn(capsuleId, userIds, pageable);

        Map<Long, User> userMap = userJpaRepository.findAllById(
                contributors.getContent().stream()
                        .map(Contributor::getUserId)
                        .toList()
                ).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return contributors.map(contributor -> {
            User user = userMap.get(contributor.getUserId());
            return ContributorResponseDto.builder()
                    .contributorRole(contributor.getContributorRole())
                    .nickname(user.getNickname())
                    .userId(user.getId())
                    .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                    .userActiveStatus(user.getUserActiveStatus())
                    .isMe(user.getId().equals(currentUserId))
                    .build();
        });
    }
}
