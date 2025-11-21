package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.controller.payload.ChatPayload;
import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.model.ChatSession;
import com.divjazz.recommendic.chat.model.Message;
import com.divjazz.recommendic.chat.repository.ChatMessageRepository;
import com.divjazz.recommendic.chat.repository.ChatSessionRepository;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.enums.UserType;
import com.divjazz.recommendic.user.model.userAttributes.UserName;
import com.divjazz.recommendic.user.repository.ConsultantProfileRepository;
import com.divjazz.recommendic.user.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthUtils authUtils;
    private final PatientProfileRepository patientProfileRepository;
    private final ConsultantProfileRepository consultantProfileRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Transactional
    public void sendMessage(ChatPayload chatPayload, String sessionId) {
        ChatSession chatSession = null;

        var currentUser = authUtils.getCurrentUser();
        if (currentUser.userId() == null) {
            throw new AuthenticationException("No user id found for current user");
        }
        String patientId = currentUser.userType() == UserType.PATIENT
                ? currentUser.userId()
                : chatPayload.recipientId();

        String consultantId = currentUser.userType() == UserType.CONSULTANT
                ? currentUser.userId()
                : chatPayload.recipientId();

        var chatSessionOpt = chatSessionRepository.findByChatSessionId(sessionId);

        if (chatSessionOpt.isEmpty()) {
            chatSession = new ChatSession(patientId, consultantId);
            chatSession = chatSessionRepository.save(chatSession);
        } else {
            chatSession = chatSessionOpt.get();
        }

        Optional<UserName> userName = switch (currentUser.userType()) {
            case PATIENT -> consultantProfileRepository.findUserNameByConsultant_UserId(currentUser.userId());
            case CONSULTANT -> patientProfileRepository.findUserNameByPatient_UserId(currentUser.userId());
            default -> throw new IllegalStateException("Unexpected value: %s".formatted(currentUser.userType()));
        };
        var chatMessage = ChatMessage.ofChat(
                currentUser.userId(),
                chatPayload.recipientId(),
                userName.map(UserName::getFirstName)
                        .orElse("NOT FOUND"),
                chatPayload.content(),
                LocalDateTime.parse(chatPayload.timestamp())
        );
        var message = new Message(
                currentUser.userId(),
                chatPayload.recipientId(),
                chatPayload.content(),
                chatMessage.timeStamp(),
                chatSession
        );

        try {
            simpMessagingTemplate.convertAndSend("/topic/chat/%s".formatted(sessionId), chatMessage );
            message.setDelivered(true);
            chatMessageRepository.save(message);
        } catch (MessagingException e) {
            message.setDelivered(false);
            chatMessageRepository.save(message);
        }
    }

    public void handleReconnection(String userId, String sessionId) {
        List<Message> undeliveredMessages = chatMessageRepository.findByReceiverIdAndChatSession_ChatSessionIdAndDeliveredFalse(userId, sessionId);
        List<ChatMessage> chatMessagesToBeDelivered = undeliveredMessages.stream()
                .map(this::toChatMessage)
                .toList();
        simpMessagingTemplate.convertAndSend("/topic/chat/%s".formatted(userId), chatMessagesToBeDelivered);
    }

    private ChatMessage toChatMessage(Message message) {
        var currentUser = authUtils.getCurrentUser();
        if (currentUser.userType() == null) {
            throw new AuthenticationException("No user id found for current user");
        }
        var userName = switch (currentUser.userType()) {
            case PATIENT -> patientProfileRepository
                    .findUserNameByPatient_UserId(currentUser.userId())
                    .map(UserName::getFirstName)
                    .orElse("Couldn't find name");
            case CONSULTANT ->consultantProfileRepository
                    .findUserNameByConsultant_UserId(currentUser.userId())
                    .map(UserName::getFirstName)
                    .orElse("Couldn't find name");
            default -> throw new IllegalStateException("Unexpected value:%s".formatted(currentUser.userType()));
        };
        return ChatMessage.ofChat(message.getSenderId(),
                message.getReceiverId(),
                userName,
                message.getContent(),
                message.getTimeStamp());
    }
}
