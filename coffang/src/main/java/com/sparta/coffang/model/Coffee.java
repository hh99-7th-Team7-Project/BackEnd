package com.sparta.coffang.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.repository.UserRepository;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Coffee {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "coffee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Price> prices;

    @Column(nullable = false)
    private String img;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;
    ///러브카운트
    @Column(nullable = false)
    private Long loveCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void setCoffee(CoffeeRequestDto coffeeRequestDto, String brand){
        this.name = coffeeRequestDto.getName();
        this.img = coffeeRequestDto.getImg();
        this.brand = brand;
        this.category = coffeeRequestDto.getCategory();
        //추가본@@
        this.loveCount = 0L;
    }

    public void setPrices(List<Price> prices){
        this.prices = prices;
    }

    //추가본 @@
    public void LoveCount(Long Count) {
        this.loveCount = Count;
    }

    public Long getUserId() {
            return  getUser().getId();
    }
}
