package com.sparta.coffang.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Review {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String review;// 리뷰

    @Column(nullable = false)
    private Float star;


    @ManyToOne
    @JoinColumn(name = "coffee")
    private  Coffee coffe;

    public Review(Float star,String review){
        this.star = star;
        this.review = review;

    }

}
