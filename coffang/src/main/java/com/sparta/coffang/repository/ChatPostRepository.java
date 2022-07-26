package com.sparta.coffang.repository;

import com.sparta.coffang.model.ChatPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ChatPostRepository extends JpaRepository<ChatPost,Long> {
    List<ChatPost> findAllByOrderByCreatedAtDesc();
    Collection<Object> findAllByChatpostId(Long chatpostId);

    //마이페이지 내가 참여한 채팅방 보기
    ChatPost findByChatpostId(Long chatpostId);
}
