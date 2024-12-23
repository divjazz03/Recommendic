package com.divjazz.recommendic.chat.dto;

public record ChatResponseMessage(String senderName, String receiverName, String consultationId, String content, java.time.LocalDateTime timestamp, Boolean viewed) {
}
