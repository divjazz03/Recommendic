package com.divjazz.recommendic.chat.dto;

import com.divjazz.recommendic.chat.MessageType;

import java.time.LocalDateTime;

public class ChatMessage {

    private final String senderId;
    private final String receiverId;
    private final String content;
    private final String consultationId;
    private final LocalDateTime timeStamp;
    private final MessageType messageType;



    public ChatMessage(String senderId, String receiverId, String content, String consultationId, MessageType messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.consultationId = consultationId;
        this.messageType = messageType;
        timeStamp = LocalDateTime.now();
    }

    public String getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getConsultationId() {
        return consultationId;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
