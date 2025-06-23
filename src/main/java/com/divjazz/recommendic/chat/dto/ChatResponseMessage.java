package com.divjazz.recommendic.chat.dto;

import java.time.LocalDateTime;

public record ChatResponseMessage(String senderName, String receiverName, long consultationId, String content, LocalDateTime timestamp, Boolean viewed) {
}
