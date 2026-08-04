package com.memoryseal.memorysealbackend.domain.time_capsule.service;

import com.memoryseal.memorysealbackend.domain.contributor.controller.dto.res.PageResponseDto;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.WateringDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.controller.dto.res.WateringResponseDto;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleWatering;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.WateringJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WateringService {

    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final WateringJpaRepository wateringJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;

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

    @Transactional
    public void water(Long capsuleId) {
        Long currentUserId = getCurrentUserId();

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(timeCapsule.getTimeCapsuleStatus() != TimeCapsuleStatus.BURIED) {
            throw new AuthException(ErrorCode.NOT_TIMECAPSULE_BURIED);
        }

        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(capsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        if(wateringJpaRepository.existsByTimeCapsuleIdAndWateredDate(capsuleId, LocalDate.now())) {
            throw new AuthException(ErrorCode.ALREADY_WATERED);
        }

        wateringJpaRepository.save(
                TimeCapsuleWatering.builder()
                        .timeCapsuleId(capsuleId)
                        .userId(currentUserId)
                        .wateredDate(LocalDate.now())
                        .build());
    }

    public WateringResponseDto getWatering(Long capsuleId, Pageable pageable) {
        Long currentUserId = getCurrentUserId();

        TimeCapsule timeCapsule = timeCapsuleJpaRepository.findById(capsuleId)
                .orElseThrow(() -> new AuthException(ErrorCode.TIMECAPSULE_NOT_FOUND));

        if(!contributorJpaRepository.existsByTimeCapsuleIdAndUserId(capsuleId, currentUserId)) {
            throw new AuthException(ErrorCode.ACCESS_DENIED);
        }

        LocalDate buriedDate = timeCapsule.getBuriedAt().toLocalDate();
        LocalDate openedDate = timeCapsule.getOpenedAt().toLocalDate();
        long totalDays = ChronoUnit.DAYS.between(buriedDate, openedDate);

        long wateringCount = wateringJpaRepository.countByTimeCapsuleId(capsuleId);

        double percentage = totalDays == 0 ? 0 : (double) wateringCount / totalDays * 100;
        int stage = Math.min((int) (percentage / 25) + 1, 5);

        List<TimeCapsuleWatering> allWaterings = wateringJpaRepository.findByTimeCapsuleId(capsuleId);
        Map<LocalDate, TimeCapsuleWatering> wateringMap = allWaterings.stream()
                .collect(Collectors.toMap(TimeCapsuleWatering::getWateredDate, w -> w));

        List<Long> userIds = allWaterings.stream()
                .map(TimeCapsuleWatering::getUserId)
                .distinct()
                .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.isBefore(openedDate) ? today : openedDate;

        List<LocalDate> allDates = buriedDate.datesUntil(endDate.plusDays(1))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allDates.size());
        List<LocalDate> pagedDates = start >= allDates.size() ? List.of() : allDates.subList(start, end);

        List<WateringDto> wateringDtos = pagedDates.stream()
                .map(date -> {
                    TimeCapsuleWatering watering = wateringMap.get(date);
                    if(watering == null) {
                        return WateringDto.builder()
                                .wateredDate(date)
                                .isWatered(false)
                                .build();
                    }
                    User user = userMap.get(watering.getUserId());
                    return WateringDto.builder()
                            .wateredDate(date)
                            .isWatered(true)
                            .userId(user.getId())
                            .nickname(user.getNickname())
                            .profileImageUrl(user.getProfileImage() != null ? user.getProfileImage().getFileUrl() : null)
                            .build();
                })
                .toList();

        Page<WateringDto> page = new PageImpl<>(wateringDtos, pageable, allDates.size());

        return WateringResponseDto.builder()
                .totalDays(totalDays)
                .wateringCount(wateringCount)
                .stage(stage)
                .waterings(new PageResponseDto<>(page))
                .build();
    }

}
