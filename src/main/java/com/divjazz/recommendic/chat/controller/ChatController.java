package com.divjazz.recommendic.chat.controller;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {


    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage") //Maps to "/app/sendMessage"
    public void sendMessage(ChatMessage message) {
        messagingTemplate.convertAndSendToUser(message.getReceiverId(), "/queue", message);
    }
}
