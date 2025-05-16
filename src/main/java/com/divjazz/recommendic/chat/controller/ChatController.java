package com.divjazz.recommendic.chat.controller;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/sendMessage") //Maps to "/app/sendMessage"
    public void sendMessage(@Payload ChatMessage message) {
        chatMessageService.sendMessage(message);
        CompletableFuture.runAsync(() -> chatMessageService.saveMessage(message));
    }

}
