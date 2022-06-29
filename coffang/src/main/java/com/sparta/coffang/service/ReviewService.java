package com.sparta.coffang.service;

import com.sparta.coffang.dto.ReviewRequestDto;
import com.sparta.coffang.dto.ReviewResponseDto;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.repository.CoffeeRepository;
import com.sparta.coffang.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final CoffeeRepository coffeeRepository;

    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, Long id, UserDtailsImpl userDtails) {
        Coffee coffee = coffeeRepository.findById(id).orElseThrow(
                () -> new IllegalAccessException("")
        );
        System.out.println("coffee 검색");
        Review review = new Review(reviewRequestDto.getStar(),
                coffee,
                userDtails.getUser(),
                reviewRequestDto.getReview());

        System.out.println("review 생성");
        reviewRepository.save(review);

        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(review.getId())
                .review(review.getReview())
                .build();

        System.out.println("코멘트 성공");
        return reviewResponseDto;
    }

    public List<ReviewResponseDto>findReviews(Long id) {
        List<Review> reviews = reviewRepository.findAllByCoffeeId(id);
        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .id(review.getId())
                    .review(review.getReview())
                    .build();

            reviewResponseDtos.add(reviewResponseDto);

        }
        System.out.println("코멘트 검색 성공");
        return reviewResponseDtos;
    }
    //삭제
    public void deleteComment(Long reviewId) {

        System.out.println("delete reviewId : " + reviewId);

        reviewRepository.deleteById(reviewId);

    }

    //댓글 수정
    @Transactional
    public Review updateReview(Long reviewId, ReviewRequestDto reviewRequestDto){
        Review review =reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );
        review.update(reviewRequestDto);
        return review;
    }
 }

