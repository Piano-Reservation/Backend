package com.backend_piano.notification.event;

import com.backend_piano.global.event.NotificationEvent;
import com.backend_piano.global.sse.SseEmitterManager;
import com.backend_piano.global.sse.SseEventName;
import com.backend_piano.notification.dto.NotificationResponse;
import com.backend_piano.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final SseEmitterManager sseEmitterManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationEvent event) {
        NotificationResponse response = notificationService.save(
                event.studentId(), event.type(), event.message());
        sseEmitterManager.send(event.studentId(), SseEventName.NOTIFICATION, response);
    }
}
