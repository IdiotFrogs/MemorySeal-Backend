package com.memoryseal.memorysealbackend.global.FCM;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final FirebaseMessaging firebaseMessaging;

    private void sendNotification(String fcmToken, String title, String body, Long capsuleId, String action) {
        if(fcmToken == null) {
            return;
        }
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("capsuleId", String.valueOf(capsuleId))
                    .putData("action", action)
                    .build();
            firebaseMessaging.send(message);
            log.info("푸시 알림 전송 성공: {}", fcmToken);
        }catch (FirebaseMessagingException e) {
            log.error("푸시 알림 전송 실패: {}", e.getMessage());
        }
    }


    public void sendBuriedNotification(String fcmToken, String title, Long capsuleId) {
        sendNotification(fcmToken, "타임 티켓 묻기", title + "티켓이 봉인됐어요!", capsuleId, "detail");
    }

    public void sendOpenedNotification(String fcmToken, String title, Long capsuleId) {
        sendNotification(fcmToken, "타임 티켓 개봉", title + "티켓을 확인해보세요!", capsuleId,"open");
    }

    public void sendJoinRequestNotification(String fcmToken, String title, String nickname, Long capsuleId) {
        sendNotification(fcmToken, "타임 티켓 참가", title + "티켓에 " + nickname + "님이 참가했어요!", capsuleId, "member");
    }
}
