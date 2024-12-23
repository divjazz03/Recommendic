package com.divjazz.recommendic.consultation.service;

import com.divjazz.recommendic.chat.MessageType;
import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.model.Message;
import com.divjazz.recommendic.chat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Objects;

@Component
public class ConsultationWebSocketHandler extends TextWebSocketHandler {
    private final ChatMessageRepository messageRepository;
    private final WebSocketSessionManager socketSessionManager;

    private final ObjectMapper objectMapper;

    public ConsultationWebSocketHandler(ChatMessageRepository messageRepository, WebSocketSessionManager socketSessionManager, ObjectMapper objectMapper) {
        this.messageRepository = messageRepository;
        this.socketSessionManager = socketSessionManager;
        this.objectMapper = objectMapper;
    }



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var userId = ( Objects.requireNonNull(session.getPrincipal())).getName();
        socketSessionManager.addSession(userId, session);

        List<Message> unViewedMessages = messageRepository.findAllByReceiverIdAndViewed(userId,false);

        for (Message message : unViewedMessages) {
            var chatMessage = new ChatMessage(message.getSenderId(), message.getReceiverId(), message.getContent(), message.getConsultationId(), MessageType.CHAT);
            var chatMessageJson = objectMapper.writeValueAsString(chatMessage);
            session.sendMessage(new TextMessage(chatMessageJson));
            
            message.setViewed(true);
            messageRepository.save(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        var userId = (Objects.requireNonNull(session.getPrincipal())).getName();
        socketSessionManager.removeSessions(userId);
    }
}
