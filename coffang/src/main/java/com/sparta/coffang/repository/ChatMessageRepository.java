package com.sparta.coffang.repository;

import com.sparta.coffang.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findTOP20ByChatRoom_AreaOrderByCreatedAtDesc(String area);
    List<ChatMessage> findTOP20ByChatRoom_ChatpostIdOrderByCreatedAtDesc(Long chatpostId);
//    List<ChatMessage> findTOP20ByChatRoom_UidOrderByCreatedAtDesc(Long id);
    ChatMessage findByMessageContains(String message);
//    void deleteAllByMessageContainsAndUid(String message, Long id);


}
 