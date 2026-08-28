package com.divjazz.recommendic.consultation.sse;

import com.divjazz.recommendic.consultation.event.PatientJoinedEventData;
import com.divjazz.recommendic.sse.SseManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationSSEService {
    private final SseManager sseManager;


    public void notifyConsultationEnded(String userId, String consultationId) {
        SseEmitter emitter = sseManager.getEmitter(userId);
        try {
            emitter.send(
                    SseEmitter.event()
                            .name(ConsultationSSEEvent.CONSULTATION_ENDED.name())
                            .data(consultationId)
            );
        } catch (IOException ex) {
            emitter.complete();
            sseManager.remove(userId);
        }
    }

    public void notifyPatientJoined(String consultantId,
                                    String consultationId,
                                    String patientId) {
        var emitter = sseManager.getEmitter(consultantId);
        log.info("Notifying {} that {} has joined in emitter {}", consultantId, patientId, emitter.toString());
        try {
            emitter.send(
                    SseEmitter.event()
                            .name(ConsultationSSEEvent.PATIENT_JOINED.name())
                            .data(new PatientJoinedEventData(
                                    consultationId,
                                    patientId
                            ))
            );
        } catch (IOException ex) {
            emitter.complete();
            sseManager.remove(consultantId);
        }
    }
}
