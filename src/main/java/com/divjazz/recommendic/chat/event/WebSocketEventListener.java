package com.divjazz.recommendic.chat.event;

import com.divjazz.recommendic.chat.MessageType;
import com.divjazz.recommendic.chat.dto.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

public class WebSocketEventListener {
    public static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sender_id");
        String receiver_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("receiver_id");
        String consultation_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("consultation_id");
        if (sender_id != null) {
            log.info("User Disconnected: " + sender_id);

            var chatMessage = new ChatMessage(sender_id, receiver_id, "", consultation_id, MessageType.LEAVE);
            messagingTemplate.convertAndSendToUser(receiver_id, "/queue/actions", chatMessage);
        }

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sender_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("sender_id");
        String receiver_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("receiver_id");
        String consultation_id = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("consultation_id");
        if (sender_id != null) {
            log.info("User connected: " + sender_id);

            var chatMessage = new ChatMessage(sender_id, receiver_id, "", consultation_id, MessageType.CONNECT);
            messagingTemplate.convertAndSendToUser(receiver_id, "/queue/actions", chatMessage);
        }

    }
}
