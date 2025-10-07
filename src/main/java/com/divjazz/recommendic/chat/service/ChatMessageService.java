package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.controller.payload.ChatPayload;
import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.model.Message;
import com.divjazz.recommendic.chat.repository.ChatMessageRepository;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.security.exception.AuthenticationException;
import com.divjazz.recommendic.security.utils.AuthUtils;
import com.divjazz.recommendic.user.model.Consultant;
import com.divjazz.recommendic.user.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConsultationService consultationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthUtils authUtils;

    @Transactional
    public void sendMessage(ChatPayload chatPayload, String consultationId) {
        var consultation = consultationService.getConsultationById(consultationId);
        var currentUser = authUtils.getCurrentUser();
        if (currentUser.getUserId() == null) {
            throw new AuthenticationException("No user id found for current user");
        }
        var recipientId = switch (currentUser) {
            case Patient ignored -> consultation.getAppointment().getConsultant().getUserId();
            case Consultant ignored -> consultation.getAppointment().getPatient().getUserId();
            default -> throw new IllegalStateException("Unexpected value: " + currentUser);
        };
        var userName = switch (currentUser) {
            case Patient patient ->  patient.getPatientProfile().getUserName().getFirstName();
            case Consultant consultant ->   consultant.getProfile().getUserName().getFirstName();
            default -> throw new IllegalStateException("Unexpected value: " + currentUser);
        };
        var chatMessage = ChatMessage.ofChat(
                currentUser.getUserId(),
                recipientId,
                userName,
                chatPayload.content(),
                chatPayload.consultationID(),
                LocalDateTime.parse(chatPayload.timestamp())
        );
        var message = new Message(
                currentUser.getUserId(),
                recipientId,
                consultation,
                chatPayload.content(),
                chatMessage.timeStamp()
        );
        try {
            simpMessagingTemplate.convertAndSend("/topic/chat/" + consultationId, chatMessage );
            message.setDelivered(true);
            chatMessageRepository.save(message);
        } catch (MessagingException e) {
            message.setDelivered(false);
            chatMessageRepository.save(message);
        }
    }

    public void handleReconnection(String userId, String consultationId) {
        List<Message> undeliveredMessages = chatMessageRepository.findByReceiverIdAndDeliveredFalse(userId);
        List<ChatMessage> chatMessagesToBeDelivered = undeliveredMessages.stream()
                .map(this::toChatMessage)
                .toList();
        simpMessagingTemplate.convertAndSend("/topic/chat/" + consultationId, chatMessagesToBeDelivered);

    }

    private ChatMessage toChatMessage(Message message) {
        var currentUser = authUtils.getCurrentUser();
        if (currentUser.getUserId() == null) {
            throw new AuthenticationException("No user id found for current user");
        }
        var userName = switch (currentUser) {
            case Patient patient ->  patient.getPatientProfile().getUserName().getFirstName();
            case Consultant consultant ->   consultant.getProfile().getUserName().getFirstName();
            default -> throw new IllegalStateException("Unexpected value: " + currentUser);
        };
        return ChatMessage.ofChat(message.getSenderId(),
                message.getReceiverId(),
                userName,
                message.getContent(),
                message.getConsultation().getConsultationId(),
                message.getTimeStamp());
    }
}
