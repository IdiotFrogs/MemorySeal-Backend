package com.memoryseal.memorysealbackend.domain.time_capsule.scheduler;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
import com.memoryseal.memorysealbackend.domain.time_capsule.repository.TimeCapsuleJpaRepository;
import com.memoryseal.memorysealbackend.domain.user.entity.User;
import com.memoryseal.memorysealbackend.domain.user.repository.UserJpaRepository;
import com.memoryseal.memorysealbackend.global.FCM.FCMService;
import com.memoryseal.memorysealbackend.global.error.ErrorCode;
import com.memoryseal.memorysealbackend.global.error.Exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeCapsuleScheduler {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FCMService fcmService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void sendOpenNotification() {
        LocalDate today = LocalDate.now();

        List<TimeCapsule> capsules = timeCapsuleJpaRepository
                .findByOpenedAtAndTimeCapsuleStatus(
                        today,
                        TimeCapsuleStatus.BURIED
                );

        capsules.forEach(capsule -> {
            capsule.setTimeCapsuleStatus(TimeCapsuleStatus.OPENED);

            List<Contributor> contributors = contributorJpaRepository
                    .findByTimeCapsuleId(capsule.getId());

            List<Long> userIds = contributors.stream()
                            .map(Contributor::getUserId)
                            .toList();

            Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));

            userMap.values().forEach(user ->
                    fcmService.sendOpenedNotification(user.getFcmToken(),capsule.getTitle(), capsule.getId()));
        });

        timeCapsuleJpaRepository.saveAll(capsules);
        log.info("타임캡슐 오픈 알림 전송 완료: {}개", capsules.size());
    }
}
