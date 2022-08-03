package com.sparta.coffang.service;


import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LoveService {
    private final LoveRepository loveRepository;
    private final CoffeeRespoistory coffeeRespoistory;

    //) -> {throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);}
//.orElseThrow(() -> new IllegalArgumentException(""))
    @Transactional
    public ResponseEntity Love(User user, String brand, Long coffeeid) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, coffeeid);

        Love existLove = loveRepository.findByUserIdAndCoffeeId(user.getId(), coffeeid);

        if (loveRepository.existsByUserNicknameAndCoffeeId(user.getNickname(), coffeeid)) {
            loveRepository.deleteById(existLove.getLoveId());
        }
        //love 이미 존재하지 않으면 생성
        else {
            Love love = new Love(user, coffee);

            loveRepository.save(love);
            System.out.println(love.getLoveId());
            System.out.println(coffee.getId() + coffee.getName());
            System.out.println(coffee.getLoveList().size());
            System.out.println("Love 생성");
        }
    return ResponseEntity.ok().body(loveRepository.existsByUserNicknameAndCoffeeId(user.getNickname(), coffeeid));
    }
}


