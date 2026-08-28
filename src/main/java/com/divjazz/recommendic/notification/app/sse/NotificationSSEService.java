package com.divjazz.recommendic.notification.app.sse;

import com.divjazz.recommendic.notification.app.dto.NotificationDTO;
import com.divjazz.recommendic.sse.SseManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationSSEService {
    private final SseManager sseManager;

    public void notifyUser(String userId, NotificationDTO data) {
        SseEmitter emitter = sseManager.getEmitter(userId);
        try {
            emitter.send(
                    SseEmitter.event()
                            .name(NotificationSSEEvent.NOTIFICATION.name())
                            .data(data)
            );
        } catch (IOException ex) {
            emitter.complete();
            sseManager.remove(userId);
        }
    }

}
