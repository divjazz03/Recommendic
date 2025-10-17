package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.repository.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final AuthUtils authUtils;

    @EventListener
    public void handleWebsocketConnectionListener(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String consultationId = sha.getFirstNativeHeader("consultationId");
        UserDTO currentUser = authUtils.getCurrentUser();
        ChatMessage chatMessage;
        switch (currentUser.userType()) {
            case PATIENT -> {
                var username = "Patient name";
                log.info("User {} connected to consultation {}",username , consultationId);
                chatMessage = ChatMessage.ofConnect("user %s has connected".formatted(username));
            }
            case CONSULTANT -> {
                var username = "Consultant name";
                log.info("User {} connected to consultation {}",username , consultationId);
                chatMessage = ChatMessage.ofConnect("user %s has connected".formatted(username));
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentUser);
        }
        // Optionally, store session info in a map to track who is online
        // e.g., Map<sessionId, ConsultationContext>
        messagingTemplate.convertAndSend("/topic/chat/" + consultationId, chatMessage);
        chatMessageService.handleReconnection(currentUser.userId(), consultationId);
    }
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String consultationId = sha.getFirstNativeHeader("consultationId");
        String sessionId = sha.getSessionId();
        UserDTO currentUser = authUtils.getCurrentUser();
        ChatMessage chatMessage;
        switch (currentUser.userType()) {
            case PATIENT -> {
                var username = "Patient name";
                log.info("User {} disconnected from consultation {}",username , consultationId);
                chatMessage = ChatMessage.ofLeave("user %s has left".formatted(username));
            }
            case CONSULTANT -> {
                var username = "Consultant name";
                log.info("User {} disconnected from consultation {}",username , consultationId);
                chatMessage = ChatMessage.ofConnect("user %s has left".formatted(username));
            }
            default -> throw new IllegalStateException("Unexpected value: " + currentUser);
        }
        // Optionally, store session info in a map to track who is online
        // e.g., Map<sessionId, ConsultationContext>
        messagingTemplate.convertAndSend("/topic/chat/" + consultationId, chatMessage);
        log.info("Session {} disconnected", sessionId);
    }


}
