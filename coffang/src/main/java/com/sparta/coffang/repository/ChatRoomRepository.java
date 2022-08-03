package com.sparta.coffang.repository;

import com.sparta.coffang.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
    Optional<ChatRoom> findByChatpostId(Long chatpostId);



}
