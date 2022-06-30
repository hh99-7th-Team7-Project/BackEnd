package com.sparta.coffang.model;

import com.sparta.coffang.dto.LoveDto;
import lombok.NoArgsConstructor;
import lombok.Getter;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Getter
public class Love {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long CoffeeId;

    @Column(nullable = false)
    private Boolean is_check;

    public Love(long coffeeId, LoveDto loveDto){
        this.CoffeeId = coffeeId;
        this.userId = loveDto.getUserId();
        this.is_check = loveDto.getIs_Check();
    }
    public void update(LoveDto loveDto){
        this.is_check = loveDto.getIs_Check();
    }

    public void Is_check(boolean b) {
        this.is_check = b;

    }

    }

