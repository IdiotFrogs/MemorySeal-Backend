package com.memoryseal.memorysealbackend.domain.seasonal_push.scheduler;

import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.Season;
import com.memoryseal.memorysealbackend.domain.seasonal_push.entity.SeasonalPush;
import com.memoryseal.memorysealbackend.domain.seasonal_push.repository.SeasonalPushJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.FCM.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeasonalPushScheduler {
    private final SeasonalPushJpaRepository pushJpaRepository;
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FCMService fcmService;
    private final Random random = new Random();

    @Transactional
    @Scheduled(cron = "0 0 0 1 3,6,9,12 *", zone = "Asia/Seoul")
    public void sendSeasonalPush() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Season currentSeason = Season.from(today.getMonthValue());
        int currentYear = today.getYear();

        LocalDateTime start = getSeasonStart(currentSeason, currentYear);
        LocalDateTime end = getSeasonEnd(currentSeason, currentYear);

        Set<Long> alreadySentUserIds = pushJpaRepository.findAlreadySentUserIds(currentSeason, currentYear);

        List<TimeCapsule> openedCapsules = timeCapsuleJpaRepository.findOpenedInSeason(start, end);

        Map<Long, List<TimeCapsule>> userCapsulesMap = openedCapsules.stream()
                .filter(timeCapsule -> !alreadySentUserIds.contains(timeCapsule.getUserId()))
                .collect(Collectors.groupingBy(TimeCapsule::getUserId));

        if(userCapsulesMap.isEmpty()) {
            return;
        }

        List<Long> targetUserIds = new ArrayList<>(userCapsulesMap.keySet());
        Map<Long, User> userMap = userJpaRepository.findAllById(targetUserIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<SeasonalPush> save = new ArrayList<>();

        userMap.values().forEach(user -> {
            String fcmToken = user.getFcmToken();
            if(fcmToken == null || fcmToken.isBlank()) {
                return;
            }

            List<TimeCapsule> userCapsules = userCapsulesMap.get(user.getId());
            if(userCapsules == null || userCapsules.isEmpty()) {
                return;
            }

            TimeCapsule selected = userCapsules.get(random.nextInt(userCapsules.size()));

            fcmService.sendSeasonalNotification(fcmToken, user.getId(), selected.getId(), currentSeason.getContent());

            save.add(SeasonalPush.builder()
                    .userId(user.getId())
                    .season(currentSeason)
                    .year(currentYear)
                    .timeCapsuleId(selected.getId())
                    .sentAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                    .build());
        });
        pushJpaRepository.saveAll(save);

    }

    private LocalDateTime getSeasonStart(Season season, int year) {
        return switch (season) {
            case SPRING -> LocalDate.of(year, 3, 1).atStartOfDay();
            case SUMMER -> LocalDate.of(year, 6, 1).atStartOfDay();
            case FALL -> LocalDate.of(year, 9, 1).atStartOfDay();
            case WINTER -> LocalDate.of(year, 12, 1).atStartOfDay();
        };
    }

    private LocalDateTime getSeasonEnd(Season season, int year) {
        return getSeasonStart(season, year).plusMonths(3).minusSeconds(1);
    }
}
