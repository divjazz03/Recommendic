package com.divjazz.recommendic.chat.model;

import com.divjazz.recommendic.global.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "chat_session")
public class ChatSession extends Auditable {
    @Generated(event = EventType.INSERT)
    @Column(name = "chat_session_id")
    private String chatSessionId;
    @Column(name = "patient_id")
    private String patientId;
    @Column(name = "consultant_id")
    private String consultantId;

    protected ChatSession() {
    }

    public ChatSession(String patientId, String consultantId) {
        this.patientId = patientId;
        this.consultantId = consultantId;
    }
}
