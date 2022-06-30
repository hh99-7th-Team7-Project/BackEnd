package com.sparta.coffang.controller;

import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.service.LoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoveController {

    private final LoveRepository loveRepository;
    private final LoveService loveService;

    @Autowired
    public LoveController(LoveRepository loveRepository, LoveService loveService) {
        this.loveRepository = loveRepository;
        this.loveService = loveService;
    }

    //클라이언트가 관심 눌럿을 때
    @PutMapping("/coffee/{brand}/{id}/love")
    public Long updatelove(@PathVariable Long coffeeId, @RequestBody LoveDto loveDto) {
        //객체가 있으면 수정
        if (loveRepository.findByUserIdAndCoffeeId(loveDto.getUserId(), coffeeId).isPresent()) {
            Long loveCount = loveService.updatelove(coffeeId, loveDto);
            return loveCount;
        }
        //객체가 없으면 생성
        else {
            Long loveCount = loveService.createlove(coffeeId, loveDto);
            return loveCount;
        }

    }
}



