package com.memoryseal.memorysealbackend.domain.time_capsule.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleCreateResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleNameDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleUpdateResDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.global.aws.service.S3Service;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeCapsuleService {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
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

    public TimeCapsuleCreateResDto createTimeCapsule(TimeCapsuleCreateDto timeCapsuleCreateDto, MultipartFile mainImage) throws IOException {
        Long currentUserId = getCurrentUserId();

        log.info("타임캡슐 생성 시작 - 유저 ID: {}", currentUserId);

        if(timeCapsuleCreateDto.getOpenedAt().isBefore(LocalDateTime.now())) {
            throw new AuthException(ErrorCode.INVALID_OPENED_AT);
        }

        try{
            TimeCapsule timeCapsule = TimeCapsule.builder()
                    .title(timeCapsuleCreateDto.getTitle())
                    .description(timeCapsuleCreateDto.getDescription())
                    .openedAt(timeCapsuleCreateDto.getOpenedAt())
                    .timeCapsuleStatus(TimeCapsuleStatus.BEFOREBURIED)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .timeCapsuleActiveStatus(true)
                    .userId(currentUserId)
                    .build();

            TimeCapsule savedTimeCapsule = timeCapsuleJpaRepository.save(timeCapsule);
            log.info("타임캡슐 DB 저장 완료 - 생성된 ID: {}", savedTimeCapsule.getId());

            if(mainImage != null && !mainImage.isEmpty()) {
                log.info("이미지 업로드 시도: {}", mainImage.getOriginalFilename());
                s3Service.uploadImage(mainImage, savedTimeCapsule.getId());
            }

            Contributor hostContributor = Contributor.builder()
                    .contributorRole(ContributorRole.HOST)
                    .bury(false)
                    .userId(currentUserId)
                    .timeCapsuleId(savedTimeCapsule.getId())
                    .build();
            contributorJpaRepository.save(hostContributor);
            log.info("Contributor 저장 완료");

            timeCapsuleJpaRepository.save(timeCapsule);

            return TimeCapsuleCreateResDto.toDto(timeCapsule);
        } catch (Exception e) {
            log.error("타임캡슐 생성 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }

    public TimeCapsuleResponseDto getDetail(Long id) {
        Long currentUserId = getCurrentUserId();
        Contributor contributor = contributorJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, id)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id).orElseThrow(
                () -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND)
        );
        return TimeCapsuleResponseDto.builder()
                .title(timeCapsule.getTitle())
                .description(timeCapsule.getDescription())
                .createdAt(timeCapsule.getCreatedAt())
                .buriedAt(timeCapsule.getBuriedAt())
                .openedAt(timeCapsule.getOpenedAt())
                .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                .userRole(contributor.getContributorRole())
                .build();
    }

    public List<TimeCapsuleNameDto> getTimeCapsule() {
        Long currentUserId = getCurrentUserId();

        List<Contributor> contributors = contributorJpaRepository.findByUserId(currentUserId);

        List<Long> timeCapsuleIds = contributors.stream()
                .map(Contributor::getTimeCapsuleId)
                .toList();

        List<TimeCapsule> timeCapsules = timeCapsuleJpaRepository.findAllById(timeCapsuleIds);

        Map<Long, TimeCapsule> timeCapsuleMap = timeCapsules.stream()
                .collect(Collectors.toMap(TimeCapsule::getId, t -> t));

        return contributors.stream()
                .map(contributor -> {
                    TimeCapsule timeCapsule = timeCapsuleMap.get(contributor.getTimeCapsuleId());
                    if(timeCapsule == null) {
                        throw new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND);
                    }
                    return TimeCapsuleNameDto.builder()
                            .timeCapsuleId(timeCapsule.getId())
                            .title(timeCapsule.getTitle())
                            .openedAt(timeCapsule.getOpenedAt())
                            .mainImageUrl(timeCapsule.getMainImage().getFileUrl())
                            .timeCapsuleStatus(timeCapsule.getTimeCapsuleStatus())
                            .role(contributor.getContributorRole())
                            .build();
                })
                .collect(toList());
    }

    @Transactional
    public TimeCapsuleUpdateResDto updateTimeCapsule(Long capsuleId, TimeCapsuleUpdateDto timeCapsuleUpdateDto, MultipartFile mainImage) throws IOException {
        Long currentUserId = getCurrentUserId();
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId).orElseThrow(
                () -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND)
        );
        if(timeCapsule.getTimeCapsuleStatus() != TimeCapsuleStatus.BEFOREBURIED) {
            throw new AuthException(ErrorCode.ALREADY_BURIED);
        }
        if(!timeCapsule.getUserId().equals(currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(timeCapsuleUpdateDto != null) {
            if(timeCapsuleUpdateDto.getTitle() != null) {
                timeCapsule.setTitle(timeCapsuleUpdateDto.getTitle());
            }
            if(timeCapsuleUpdateDto.getDescription() != null) {
                timeCapsule.setDescription(timeCapsuleUpdateDto.getDescription());
            }
            if(timeCapsuleUpdateDto.getOpenedAt() != null) {
                if(timeCapsuleUpdateDto.getOpenedAt().isBefore(LocalDateTime.now())) {
                    throw new AuthException(ErrorCode.INVALID_OPENED_AT);
                }
                timeCapsule.setOpenedAt(timeCapsuleUpdateDto.getOpenedAt());
            }
        }

        timeCapsule.setUpdatedAt(LocalDateTime.now());

        List<Contributor> contributors = contributorJpaRepository.findByTimeCapsuleId(capsuleId);
        contributors.forEach(c -> c.setBury(false));

        if(mainImage != null && !mainImage.isEmpty()) {
            log.info("이미지 업로드 시도: {}", mainImage.getOriginalFilename());
            s3Service.uploadImage(mainImage, capsuleId);
        }

        timeCapsuleJpaRepository.save(timeCapsule);

        return TimeCapsuleUpdateResDto.toDto(timeCapsule);
    }

    @Transactional
    public void deleteCapsule(Long capsuleId) {
        Long currentUserId = getCurrentUserId();

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        Contributor contributor = contributorJpaRepository.findByUserIdAndTimeCapsuleId(currentUserId, capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.ACCESS_DENIED));
        if(contributor.getContributorRole() != ContributorRole.HOST) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(timeCapsule.getMainImage() != null) {
            s3Service.deleteFileFromS3(timeCapsule.getMainImage().getFileUrl());
        }
        timeCapsule.getContents().forEach(content -> {
            content.getAttachedFiles().forEach(file -> {
                s3Service.deleteFileFromS3(file.getFileUrl());
            });
        });

        contributorJpaRepository.deleteByTimeCapsuleId(capsuleId);

        timeCapsuleJpaRepository.delete(timeCapsule);
    }



}
