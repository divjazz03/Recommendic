package com.divjazz.recommendic.chat.model;

import com.divjazz.recommendic.global.Auditable;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "chat_sessions")
@Getter
@NoArgsConstructor
public class ChatSession extends Auditable {
    @Column(name = "chat_session_id")
    private final String chatSessionId = "CHT_SESS-" + UlidCreator.getMonotonicUlid();
    @Column(name = "patient_id")
    private String patientId;
    @Column(name = "consultant_id")
    private String consultantId;

    public ChatSession (String patientId, String consultantId) {
        this.patientId = patientId;
        this.consultantId = consultantId;
    }

}
