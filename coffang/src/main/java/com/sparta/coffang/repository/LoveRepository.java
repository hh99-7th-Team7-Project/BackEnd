package com.sparta.coffang.repository;

import com.sparta.coffang.model.Love;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoveRepository extends JpaRepository<Love , Long> {
    Boolean existsByUserNicknameAndCoffeeId(String nickname, Long id);
    Love findByUserIdAndCoffeeId(Long userId, Long coffeeId);
    Love findByUserId(Long userId);


    //내가 북마크한 커피
    List<Love> findAllByUserId(Long userId);
    List<Love> findAllByUserNickname(String nickname);

}