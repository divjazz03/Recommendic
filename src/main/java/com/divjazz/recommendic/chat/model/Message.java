package com.divjazz.recommendic.chat.model;

import com.divjazz.recommendic.global.Auditable;
import com.divjazz.recommendic.consultation.model.Consultation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Entity
@Table(name = "message")
public class Message extends Auditable {
    @Column(name = "sender_id", updatable = false, nullable = false)
    private String senderId;
    @Column(name = "receiver_id", updatable = false, nullable = false)
    private String receiverId;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Setter
    private String content;
    @JoinColumn(name = "consultation_id")
    @ManyToOne
    private Consultation consultation;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;
    @Column(name = "delivered", nullable = false)
    @Setter
    private boolean delivered;

    protected Message() {
    }

    public Message(String senderId, String receiverId, Consultation consultation, String content, LocalDateTime timeStamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.consultation = consultation;
        this.timeStamp = Objects.isNull(timeStamp)? LocalDateTime.now() : timeStamp;
        delivered = false;
    }
}
