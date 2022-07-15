package com.sparta.coffang.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
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

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.ALL)
    private List<Price> prices;

    @Column(nullable = false)
    private String img;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private boolean lovecheck;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.REMOVE)
    private List<Love> loveList;



    public void setCoffee(CoffeeRequestDto coffeeRequestDto, String brand, List<PhotoDto> photoDtos) {
        this.name = coffeeRequestDto.getName();
        this.img = photoDtos.get(0).getPath();
        this.brand = brand;
        this.category = coffeeRequestDto.getCategory();

    }

    public void setPrices(List<Price> prices) {
        this.prices = prices;
    }

    public void addLove(Love love){
        this.loveList.add(love);
    }


}

