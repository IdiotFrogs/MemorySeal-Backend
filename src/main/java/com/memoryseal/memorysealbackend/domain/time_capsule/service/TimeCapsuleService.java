package com.memoryseal.memorysealbackend.domain.time_capsule.service;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.entity.ContributorRole;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleCreateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.req.TimeCapsuleUpdateDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleNameDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.TimeCapsuleResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeCapsuleService {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자");
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof UserDetails) {
            return Long.valueOf(((UserDetails) principal).getUsername());
        }else if(principal instanceof Long) {
            return (Long) principal;
        }else if(principal instanceof String) {
            return Long.valueOf((String) principal);
        }else {
            throw new IllegalStateException("사용자 ID를 가져올 수 없음");
        }
    }

    public TimeCapsule createTimeCapsule(TimeCapsuleCreateDto timeCapsuleCreateDto) {
        Long currentUserId = getCurrentUserId();
        TimeCapsule timeCapsule = TimeCapsule.builder()
                .title(timeCapsuleCreateDto.getTitle())
                .buriedAt(timeCapsuleCreateDto.getBuriedAt())
                .openedAt(timeCapsuleCreateDto.getOpenedAt())
                .timeCapsuleStatus(timeCapsuleCreateDto.getTimeCapsuleStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .timeCapsuleActiveStatus(timeCapsuleCreateDto.getTimeCapsuleActiveStatus())
                .userId(timeCapsuleCreateDto.getUserId())
                .build();

        Contributor hostContributor = Contributor.builder()
                .contributorRole(ContributorRole.HOST)
                .bury(false)
                .userId(currentUserId)
                .timeCapsuleId(timeCapsule.getId())
                .build();
        contributorJpaRepository.save(hostContributor);

        return timeCapsuleJpaRepository.save(timeCapsule);
    }
    public List<TimeCapsuleResponseDto> getMyDetail() {
        Long currentUserId = getCurrentUserId();

        List<TimeCapsule> myTimeCapsule = timeCapsuleJpaRepository.findByUserId(currentUserId);
        return myTimeCapsule.stream()
                .map(TimeCapsuleResponseDto::toDto)
                .collect(Collectors.toList());
    }

    public TimeCapsuleResponseDto getDetail(Long id) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        return TimeCapsuleResponseDto.toDto(timeCapsule);
    }

    public List<TimeCapsuleNameDto> getTimeCapsule() {
        Long currentUserId = getCurrentUserId();

        List<Contributor> contributors = contributorJpaRepository.findByUserId(currentUserId);

        return contributors.stream()
                .map(contributor -> {
                    TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(contributor.getTimeCapsuleId())
                            .orElseThrow(() -> new IllegalArgumentException("타임캡슐을 찾을 수 없음"));

                    return TimeCapsuleNameDto.builder()
                            .timeCapsuleId(timeCapsule.getId())
                            .title(timeCapsule.getTitle())
                            .role(contributor.getContributorRole())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public TimeCapsuleUpdateDto updateTimeCapsule(Long id, TimeCapsuleUpdateDto timeCapsuleUpdateDto) {
        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("아이디 값이 없습니다")
        );
        if(timeCapsuleUpdateDto.getTitle() != null) {
            timeCapsule.setTitle(timeCapsuleUpdateDto.getTitle());
        }
        if(timeCapsuleUpdateDto.getBuriedAt() != null) {
            timeCapsule.setBuriedAt(timeCapsuleUpdateDto.getBuriedAt());
        }
        if(timeCapsuleUpdateDto.getOpenedAt() != null) {
            timeCapsule.setOpenedAt(timeCapsuleUpdateDto.getOpenedAt());
        }
        if(timeCapsuleUpdateDto.getTimeCapsuleStatus() != null) {
            timeCapsule.setTimeCapsuleStatus(timeCapsuleUpdateDto.getTimeCapsuleStatus());
        }

        timeCapsule.setUpdatedAt(LocalDateTime.now());

        timeCapsuleJpaRepository.save(timeCapsule);

        return TimeCapsuleUpdateDto.toDto(timeCapsule);
    }


}
