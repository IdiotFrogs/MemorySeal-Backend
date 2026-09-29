package com.memoryseal.memorysealbackend.domain.time_capsule.scheduler;

import com.memoryseal.memorysealbackend.domain.contributor.entity.Contributor;
import com.memoryseal.memorysealbackend.domain.contributor.repository.ContributorJpaRepository;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsule;
import com.memoryseal.memorysealbackend.domain.time_capsule.entity.TimeCapsuleStatus;
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
import java.time.ZoneId;
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

    @Transactional
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void sendOpenNotification() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        List<TimeCapsule> capsules = timeCapsuleJpaRepository.findByOpenedAtAndTimeCapsuleStatus(today, TimeCapsuleStatus.BURIED);

        if(capsules.isEmpty()) {
            return;
        }

        capsules.forEach(capsule -> capsule.setTimeCapsuleStatus(TimeCapsuleStatus.OPENED));

        List<Long> capsuleIds = capsules.stream()
                .map(TimeCapsule::getId)
                .toList();

        List<Contributor> allContributors = contributorJpaRepository.findByTimeCapsuleIdIn(capsuleIds);

        Map<Long, List<Contributor>> contributorByCapsuleId = allContributors.stream()
                        .collect(Collectors.groupingBy(Contributor::getTimeCapsuleId));

        List<Long> userIds = allContributors.stream()
                        .map(Contributor::getUserId)
                        .distinct()
                        .toList();

        Map<Long, User> userMap = userJpaRepository.findAllById(userIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));

        capsules.forEach(capsule -> {
            List<Contributor> contributors = contributorByCapsuleId.getOrDefault(capsule.getId(), List.of());
            contributors.forEach(contributor -> {
                User user = userMap.get(contributor.getUserId());
                if(user == null || user.getFcmToken() == null) {
                    return;
                }
                try {
                    fcmService.sendOpenedNotification(user.getFcmToken(), capsule.getTitle(), capsule.getId());
                }catch (Exception e) {
                    log.warn("알림 발송 실패: userId={}, capsuleId={}", user.getId(), capsule.getId(), e);
                }
            });
        });
        log.info("타임캡슐 오픈 알림 전송 완료: {}개", capsules.size());
    }
}
