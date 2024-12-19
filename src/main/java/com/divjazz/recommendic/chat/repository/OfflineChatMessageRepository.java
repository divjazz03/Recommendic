package com.divjazz.recommendic.chat.repository;

import com.divjazz.recommendic.chat.model.OfflineMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfflineChatMessageRepository extends JpaRepository<OfflineMessage, Long> {
    List<OfflineMessage> findByReceiverId(String receiver);
    List<OfflineMessage> findAllByReceiverIdAndViewed(String receiverId, boolean viewed);
}
