package com.divjazz.recommendic.chat.controller;

import com.divjazz.recommendic.chat.controller.payload.ChatPayload;
import com.divjazz.recommendic.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat/{consultationId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT') || hasAuthority('ROLE_PATIENT')")
    public void sendMessage(@Payload ChatPayload payload, @DestinationVariable String consultationId) {
        chatMessageService.sendMessage(payload,consultationId);
    }

}
