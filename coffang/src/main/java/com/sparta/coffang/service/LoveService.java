package com.sparta.coffang.service;

import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.repository.LoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoveService {

    private final LoveRepository loveRepository;
    private final CoffeeService coffeeService;

    @Autowired
    public LoveService(LoveRepository loveRepository, CoffeeService coffeeService){

        this.coffeeService=coffeeService;
        this.loveRepository=loveRepository;
    }


    // 관심 객체 없을 때 (처음 눌렀을 때)
    public Long createlove(Long coffeeId, LoveDto loveDto){
        Love love = new Love(coffeeId, loveDto);
        loveRepository.save(love); // repository에는 Love 객체 자체가 들어가야만 된다.
        System.out.println(coffeeId +"관심 눌러서 객체 생성");

        //공감 카운트 수 업데이트 기능
        Long userId = loveDto.getUserId();
        Long loveCount = coffeeService.loveCount(coffeeId,userId);

        return loveCount;
    }

    // 관심 객체 생성 후 클릭 시
    public Long updatelove(Long coffeeId, LoveDto loveDto) {
        Love love = loveRepository.findByUserIdAndCoffeeId(loveDto.getUserId(), coffeeId).orElseThrow(
                ()-> new NullPointerException("아이디가 존재하지 않습니다.")
        );
        love.update(loveDto);
        loveRepository.save(love);

        //공감 카운트 수 업데이트 기능
        Long userId = loveDto.getUserId();
        Long loveCount = coffeeService.loveCount(coffeeId,userId);

        return loveCount;
    }

    }

