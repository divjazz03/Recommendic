package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.dto.UserDTO;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
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
    private final PatientProfileRepository patientProfileRepository;
    private final ConsultantProfileRepository consultantProfileRepository;

    @EventListener
    public void handleWebsocketConnectionListener(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = sha.getFirstNativeHeader("sessionId");
        UserDTO currentUser = authUtils.getCurrentUser();
        ChatMessage chatMessage;
        String username;
        switch (currentUser.userType()) {
            case PATIENT -> {
                username = patientProfileRepository.findUserNameByPatient_UserId(currentUser.userId())
                        .map(UserName::getFirstName)
                        .orElse("Patient name");

                chatMessage = ChatMessage.ofConnect("user %s has connected".formatted(username));
            }
            case CONSULTANT -> {
                username = consultantProfileRepository
                        .findUserNameByConsultant_UserId(currentUser.userId())
                        .map(UserName::getFirstName)
                        .orElse("Consultant name");
                chatMessage = ChatMessage.ofConnect("user %s has connected".formatted(username));
            }
            default -> throw new IllegalStateException("Unexpected value: %s" + currentUser.userType());
        }
        log.info("User {} connected to session {}", username, sessionId);
        // Optionally, store session info in a map to track who is online
        // e.g., Map<sessionId, ConsultationContext>
        messagingTemplate.convertAndSend("/topic/chat/%s".formatted(sessionId), chatMessage);
        chatMessageService.handleReconnection(currentUser.userId(), sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String chatSessionId = sha.getFirstNativeHeader("chatSessionId");
        String sessionId = sha.getSessionId();
        UserDTO currentUser = authUtils.getCurrentUser();
        ChatMessage chatMessage;
        String username;
        var disconnectedFormat = "User {} disconnected from consultation {}";
        switch (currentUser.userType()) {
            case PATIENT -> {
                username = patientProfileRepository.findUserNameByPatient_UserId(currentUser.userId())
                        .map(UserName::getFirstName)
                        .orElse("Patient name");
                chatMessage = ChatMessage.ofLeave("user %s has left".formatted(username));
            }
            case CONSULTANT -> {
                username = consultantProfileRepository.findUserNameByConsultant_UserId(currentUser.userId())
                        .map(UserName::getFirstName)
                        .orElse("Consultant name");

                chatMessage = ChatMessage.ofConnect("user %s has left".formatted(username));
            }
            default -> throw new IllegalStateException("Unexpected value: %s".formatted(currentUser));
        }
        log.info(disconnectedFormat, username, chatSessionId);
        // Optionally, store session info in a map to track who is online
        // e.g., Map<sessionId, ConsultationContext>
        messagingTemplate.convertAndSend("/topic/chat/%s".formatted(chatSessionId), chatMessage);
        log.info("Session {} disconnected", sessionId);
    }


}
