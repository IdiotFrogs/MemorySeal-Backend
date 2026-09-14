package com.memoryseal.memorysealbackend.domain.seasonal_push.service;

import com.memoryseal.memorysealbackend.domain.seasonal_push.controller.dto.res.SeasonalBannerResponse;
import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.Season;
import com.memoryseal.memorysealbackend.domain.seasonal_push.repository.SeasonalPushJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SeasonalPushService {
    private final SeasonalPushJpaRepository seasonalPushJpaRepository;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;

    public SeasonalBannerResponse getSeasonalBanner(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Season currentSeason = Season.from(today.getMonthValue());
        int currentYear = today.getYear();

        return seasonalPushJpaRepository.findByUserIdAndSeasonAndYear(userId, currentSeason, currentYear)
                .filter(h -> h.getConfirmedAt() == null)
                .filter(h -> timeCapsuleJpaRepository.existsById(h.getTimeCapsuleId()))
                .map(h -> SeasonalBannerResponse.builder()
                        .content(currentSeason.getContent())
                        .season(currentSeason)
                        .capsuleId(h.getTimeCapsuleId())
                        .build())
                .orElse(SeasonalBannerResponse.empty());
    }
}
