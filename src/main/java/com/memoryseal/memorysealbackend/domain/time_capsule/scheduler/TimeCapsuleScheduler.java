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

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeCapsuleScheduler {
    private final TimeCapsuleJpaRepository timeCapsuleJpaRepository;
    private final ContributorJpaRepository contributorJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FCMService fcmService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sendOpenNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<TimeCapsule> capsules = timeCapsuleJpaRepository
                .findByOpenedAtBetweenAndTimeCapsuleStatus(
                        startOfDay,
                        endOfDay,
                        TimeCapsuleStatus.BURIED
                );

        capsules.forEach(capsule -> {
            capsule.setTimeCapsuleStatus(TimeCapsuleStatus.OPENED);

            List<Contributor> contributors = contributorJpaRepository
                    .findByTimeCapsuleId(capsule.getId());

            contributors.forEach(c -> {
                User user = userJpaRepository.findById(c.getUserId())
                        .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
                fcmService.sendOpenedNotification(user.getFcmToken(), capsule.getTitle());
            });
        });

        timeCapsuleJpaRepository.saveAll(capsules);
        log.info("타임캡슐 오픈 알림 전송 완료: {}개", capsules.size());
    }
}
