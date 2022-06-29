package com.sparta.coffang.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
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

    public void setCoffee(CoffeeRequestDto coffeeRequestDto, String brand){
        this.name = coffeeRequestDto.getName();
        this.img = coffeeRequestDto.getImg();
        this.brand = brand;
        this.category = coffeeRequestDto.getCategory();
    }

    public void setPrices(List<Price> prices){
        this.prices = prices;
    }
}
