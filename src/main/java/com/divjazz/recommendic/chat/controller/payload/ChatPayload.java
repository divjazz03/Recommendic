package com.divjazz.recommendic.chat.controller.payload;

public record ChatPayload(String content, String recipientId,String sessionId, String timestamp) {
}
