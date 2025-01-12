package com.divjazz.recommendic.chat.controller;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class ChatController {


    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/sendMessage") //Maps to "/app/sendMessage"
    public void sendMessage(@Payload ChatMessage message) {
        chatMessageService.sendMessage(message);
    }

    @MessageMapping("/connect")
    public void connectToChat(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        Objects.requireNonNull(simpMessageHeaderAccessor.getSessionAttributes()).put("sender_id", chatMessage.getSenderId());
        Objects.requireNonNull(simpMessageHeaderAccessor.getSessionAttributes()).put("receiver_id", chatMessage.getReceiverId());
        Objects.requireNonNull(simpMessageHeaderAccessor.getSessionAttributes()).put("consultation_id", chatMessage.getConsultationId());
        messagingTemplate.convertAndSendToUser(chatMessage.getReceiverId(), "/queue/actions", chatMessage);
    }

}
