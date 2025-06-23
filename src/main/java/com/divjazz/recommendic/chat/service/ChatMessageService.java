package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.dto.ChatResponseMessage;
import com.divjazz.recommendic.chat.model.Message;
import com.divjazz.recommendic.chat.repository.ChatMessageRepository;
import com.divjazz.recommendic.consultation.service.ConsultationService;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConsultationService consultationService;
    private final GeneralUserService userService;


    public void saveMessage(ChatMessage chatMessage) {
        var consultation = consultationService.getConsultationById(chatMessage.getConsultationId());
        var offlineMessage = new Message(chatMessage.getSenderId(), chatMessage.getReceiverId(), consultation, chatMessage.getContent());
        chatMessageRepository.save(offlineMessage);
    }

    @Transactional
    public void sendMessage(ChatMessage chatMessage) {
        var consultation = consultationService.getConsultationById(chatMessage.getConsultationId());
        var message = new Message(chatMessage.getSenderId(), chatMessage.getReceiverId(), consultation, chatMessage.getContent());
        try {
            message.setDelivered(true);
            chatMessageRepository.save(message);
        } catch (MessagingException e) {
            message.setDelivered(false);
            chatMessageRepository.save(message);
        }
    }

    public void handleReconnection(String userId) {
        List<Message> undeliveredMessages = chatMessageRepository.findByReceiverIdAndDeliveredFalse(userId);

    }

    public List<ChatResponseMessage> getAllOfflineMessages(User recipient) {
        var offlineMessages = chatMessageRepository.findByReceiverId(recipient.getUserId());
        return offlineMessages.stream().map(message -> new ChatResponseMessage(
                userService.retrieveUserByUserId(message.getSenderId()).getUserNameObject().getFullName(),
                userService.retrieveUserByUserId(recipient.getUserId()).getUserNameObject().getFullName(),
                message.getConsultation().getId(),
                message.getContent(),
                message.getTimeStamp(),
                message.isDelivered()
        )).collect(Collectors.toList());
    }
}
