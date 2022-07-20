package com.sparta.coffang.repository;

import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.PostLove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLoveRepository extends JpaRepository<PostLove , Long> {
    Boolean existsByUserNicknameAndPostId(String nickname, Long id);
    PostLove findByUserIdAndPostId(Long userId, Long coffeeId);
    PostLove findByUserId(Long userId);
}