package com.sparta.coffang.repository;

import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.PostLove;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostLoveRepository extends JpaRepository<PostLove , Long> {
    Boolean existsByUserNicknameAndPostId(String nickname, Long id);
    PostLove findByUserIdAndPostId(Long userId, Long coffeeId);
    PostLove findByUserId(Long userId);


    //내가 북마크한 커피
    List<PostLove> findAllByUserId(Long userId);
    List<PostLove> findAllByUserNickname(String nickname);

}