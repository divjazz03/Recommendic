package com.divjazz.recommendic.chat.repository;

import com.divjazz.recommendic.chat.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByChatSessionId(String chatSessionId);
}
