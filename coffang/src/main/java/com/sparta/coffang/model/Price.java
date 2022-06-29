package com.sparta.coffang.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Price {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "coffee_id")
    private Coffee coffee;

    public Price(Long price, String size, Coffee coffee){
        this.price = price;
        this.size = size;
        this.coffee = coffee;
    }

    public void setCoffee(Coffee coffee){
        this.coffee = coffee;
    }
}
