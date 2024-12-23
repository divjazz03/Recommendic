package com.divjazz.recommendic.chat.repository;

import com.divjazz.recommendic.chat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(String receiver);
    List<Message> findByReceiverIdAndDeliveredFalse(String userId);

}
