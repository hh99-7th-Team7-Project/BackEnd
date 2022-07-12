package com.sparta.coffang.dto.responseDto;

//import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.model.Coffee;
//import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoffeeResponseDto {
    private Long id;

    private String name;

    private List<Map<String, Object>> pricePair;

    private String img;

    private String brand;

    private String category;

    private int love;

    private double star;

    public CoffeeResponseDto(Coffee coffee) {
        this.id = coffee.getId();
        this.name = coffee.getName();
        this.img = coffee.getImg();
        this.brand = coffee.getBrand();
        this.category = coffee.getCategory();
        //추가
    }

    public void setLove(int loves) {
        this.love = loves;
    }
}


