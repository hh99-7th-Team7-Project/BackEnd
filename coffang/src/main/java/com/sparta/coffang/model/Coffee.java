package com.sparta.coffang.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sparta.coffang.dto.PhotoDto;
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

    @Column(nullable = false)
    private String img;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private boolean lovecheck;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.REMOVE)
    private List<Love> loveList;

    @OneToMany(mappedBy = "coffee", cascade = CascadeType.REMOVE)
    private List<Review> reviews;

    public void update(CoffeeRequestDto coffeeRequestDto, String brand, List<PhotoDto> photoDtos) {
        this.name = coffeeRequestDto.getName();
        this.img = photoDtos.get(0).getPath();
        this.brand = brand;
        this.category = coffeeRequestDto.getCategory();
//        this.size = coffeeRequestDto.getSize();
    }

    public void addLove(Love love){
        this.loveList.add(love);
    }
}

