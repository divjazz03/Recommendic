package com.divjazz.recommendic.chat.service;

import com.divjazz.recommendic.chat.model.OfflineMessage;
import com.divjazz.recommendic.chat.repository.OfflineChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfflineChatMessageService {

    private final OfflineChatMessageRepository offlineChatMessageRepository;

    public OfflineChatMessageService(OfflineChatMessageRepository offlineChatMessageRepository) {
        this.offlineChatMessageRepository = offlineChatMessageRepository;
    }

    public void saveMessage(OfflineMessage offlineMessage) {
        offlineChatMessageRepository.save(offlineMessage);
    }

    public List<OfflineMessage> getAllOfflineMessages(String receiver) {
        return offlineChatMessageRepository.findByReceiverId(receiver);
    }
    public Integer getNumberOfUnViewedMessages(String receiver) {
        return offlineChatMessageRepository.findAllByReceiverIdAndViewed(receiver,false).size();
    }
}
