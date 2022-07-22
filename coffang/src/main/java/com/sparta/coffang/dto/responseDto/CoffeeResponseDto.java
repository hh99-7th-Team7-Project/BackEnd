package com.sparta.coffang.dto.responseDto;

//import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.model.Coffee;
//import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.model.User;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeResponseDto {
    private Long id;

    private String name;

    private List<Map<String, Object>> pricePair = new ArrayList<>();

    private String img;

    private String brand;

    private String category;

    private int love;

    private boolean loveCheck;

    private double star;

    public CoffeeResponseDto(Coffee coffee) {
        this.id = coffee.getId();
        this.name = coffee.getName();
        this.img = coffee.getImg();
        this.brand = coffee.getBrand();
        this.category = coffee.getCategory();
        if (coffee.getLoveList() != null)
            this.love = coffee.getLoveList().size();
        this.loveCheck = false;
        setAvgStar(coffee.getReviews());
    }

    public void setAvgStar(List<Review> reviews){
        double star = 0;

        if (reviews == null) {
            this.star = 0;
            return;
        }

        for (Review review : reviews) {
            star += review.getStar();
        }
        star /= reviews.size();
        this.star = star;
    }

    public void setPricePair(Coffee coffee){
        HashMap<String, Object> pair = new HashMap<>();
        pair.put("size", coffee.getSize());
        pair.put("price", coffee.getPrice());
        this.pricePair.add(pair);
    }

    public void setLoveCheck(boolean loveCheck){
        this.loveCheck = loveCheck;
    }
}


