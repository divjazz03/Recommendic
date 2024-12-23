package com.divjazz.recommendic.chat.model;

import com.divjazz.recommendic.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class Message extends Auditable {
    @Column(name = "sender_id", updatable = false, nullable = false)
    private String senderId;
    @Column(name = "receiver_id", updatable = false, nullable = false)
    private String receiverId;
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "consultation_id", nullable = false)
    private String consultationId;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timeStamp;
    @Column(name = "delivered", nullable = false)
    private boolean delivered;

    protected Message() {
    }

    public Message(String senderId, String receiverId, String consultationId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.consultationId = consultationId;
        this.timeStamp = LocalDateTime.now();
        delivered = false;
    }

    public String getSenderId() {
        return senderId;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }


    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }
}
