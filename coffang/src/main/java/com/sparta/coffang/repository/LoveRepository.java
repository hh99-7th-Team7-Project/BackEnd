package com.sparta.coffang.repository;

import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//import java.util.List;
//import java.util.Optional;

public interface LoveRepository extends JpaRepository<Love , Long> {
    Boolean existsByUserNicknameAndCoffeeId(String nickname, Long id);

    Boolean existsByCoffee(Coffee coffee);

    Love findByUserIdAndCoffeeId(Long userId, Long coffeeId);

    //내가 북마크한 커피
    List<Love> findAllByUserId(Long userId);
    List<Love> findAllByUserNickname(String nickname);
}