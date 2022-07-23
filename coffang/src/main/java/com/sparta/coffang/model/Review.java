package com.sparta.coffang.model;

import com.sparta.coffang.dto.requestDto.ReviewRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Review extends Timestamped {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String review;// 리뷰

    @Column(nullable = false)
    private Float star;

    @ManyToOne
    @JoinColumn(name = "coffee_id")
    private Coffee coffee;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Review(Float star, String review, Coffee coffee, User user) {
        this.star = star;
        this.review = review;
        this.coffee = coffee;
        this.user = user;
    }


    //리뷰수정
    public void update(ReviewRequestDto reviewRequestDto) {
        this.star = reviewRequestDto.getStar();
        this.review = reviewRequestDto.getReview();
    }
}

