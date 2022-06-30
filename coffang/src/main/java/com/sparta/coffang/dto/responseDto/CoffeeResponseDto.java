package com.sparta.coffang.dto.responseDto;

//import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.model.Coffee;
//import com.sparta.coffang.model.Love;
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

    private Long loveCount;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    //loveCount
    public CoffeeResponseDto(Coffee coffee, Long loveCount) {
        this.id = coffee.getId();
        this.name = coffee.getName();
        this.img = coffee.getImg();
        this.brand = coffee.getBrand();
        this.category = coffee.getCategory();
        this.loveCount = loveCount;


    }



//    public CoffeeResponseDto(Coffee coffee, LoveDto love, String nickname) {
//        this.id = coffee.getId();
//        this.name = coffee.getName();
//        this.img = coffee.getImg();
//        this.brand = coffee.getBrand();
//        this.category = coffee.getCategory();
//        this.loveCount = coffee.getLoveCount();
//        this.user = coffee.getUser();
//
//    }
//
//
//    public Long getUserId() {
//
//        return getUser().getId(); }
}


