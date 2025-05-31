package com.divjazz.recommendic.chat.repository;

import com.divjazz.recommendic.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ChatMessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverId(String receiver);

    List<Message> findByReceiverIdAndDeliveredFalse(String userId);

    @Query(value = """
        select * from search_messages(:query, :page_number, :size)
    """, nativeQuery = true)
    Set<Message> searchMessageByQuery(@Param("query") String query, @Param("size") int size, @Param("page_number") int pageNumber);

}
