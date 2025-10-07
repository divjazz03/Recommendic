package com.divjazz.recommendic.chat.dto;

import com.divjazz.recommendic.chat.MessageType;

import java.time.LocalDateTime;


public record ChatMessage (
    String senderId,
    String receiverId,
    String senderUsername,
    String content,
    String consultationId,
    LocalDateTime timeStamp,
    MessageType messageType
    )
{

    public static ChatMessage ofLeave(String message) {
        return new ChatMessage(null,
                null,
                "SYSTEM",
                null,
                null,
                LocalDateTime.now(),
                MessageType.LEAVE);
    }
    public static ChatMessage ofConnect(String message) {
        return new ChatMessage(null,
                null,
                "SYSTEM",
                null,
                null,
                LocalDateTime.now(),
                MessageType.CONNECT);
    }
    public static ChatMessage ofChat(String senderId,String receiverId,
                                     String senderUsername,
                                     String content,
                                     String consultationId, LocalDateTime timeStamp) {
        return new ChatMessage(senderId,
                receiverId,
                senderUsername,
                content,
                consultationId,
                timeStamp,
                MessageType.CHAT);
    }
}
