package com.sparta.coffang.repository;

import com.sparta.coffang.model.ChatPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatPostRepository extends JpaRepository<ChatPost,Long> {


    List<ChatPost> findAllByOrderByCreatedAtDesc();
}
