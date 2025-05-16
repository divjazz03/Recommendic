package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.dto.ChatMessage;
import com.divjazz.recommendic.chat.dto.ChatResponseMessage;
import com.divjazz.recommendic.chat.model.Message;
import com.divjazz.recommendic.chat.repository.ChatMessageRepository;
import com.divjazz.recommendic.user.model.User;
import com.divjazz.recommendic.user.service.GeneralUserService;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final GeneralUserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, GeneralUserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    public void saveMessage(ChatMessage chatMessage) {
        var offlineMessage = new Message(chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getConsultationId(), chatMessage.getContent());
        chatMessageRepository.save(offlineMessage);
    }

    @Transactional
    public void sendMessage(ChatMessage chatMessage) {
        var message = new Message(chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getConsultationId(), chatMessage.getContent());
        try {
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId(),
                    "/queue/messages",
                    chatMessage
            );
            message.setDelivered(true);
            chatMessageRepository.save(message);
        } catch (MessagingException e) {
            message.setDelivered(false);
            chatMessageRepository.save(message);
        }
    }

    public void handleReconnection(String userId) {
        List<Message> undeliveredMessages = chatMessageRepository.findByReceiverIdAndDeliveredFalse(userId);
        for (var message : undeliveredMessages) {
            messagingTemplate.convertAndSendToUser(
                    userId,
                    "/queue/messages",
                    message
            );
        }
    }

    public List<ChatResponseMessage> getAllOfflineMessages(User recipient) {
        var offlineMessages = chatMessageRepository.findByReceiverId(recipient.getUserId());
        return offlineMessages.stream().map(message -> new ChatResponseMessage(
                userService.retrieveUserByUserId(message.getSenderId()).getUserName().getFullName(),
                userService.retrieveUserByUserId(recipient.getUserId()).getUserNameObject().getFullName(),
                message.getConsultationId(),
                message.getContent(),
                message.getTimeStamp(),
                message.isDelivered()
        )).collect(Collectors.toList());
    }
}
