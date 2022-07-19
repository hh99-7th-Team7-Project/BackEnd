package com.sparta.coffang.repository;

import com.sparta.coffang.model.BookMark;
import com.sparta.coffang.model.Love;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookMarkRepository extends JpaRepository<BookMark, Long> {
    Boolean existsByUserNicknameAndPostId(String nickname, Long id);
    BookMark findByUserIdAndPostId(Long userId, Long postId);


    //내가 북마크한 커피
    List<BookMark> findAllByUserId(Long userId);
    List<BookMark> findAllByUserNickname(String nickname);

}