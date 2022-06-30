package com.sparta.coffang.service;

import com.sparta.coffang.dto.ReviewRequestDto;
import com.sparta.coffang.dto.ReviewResponseDto;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.ReviewRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final CoffeeRespoistory coffeeRespoistory;







    //리뷰 생성

    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, Long id, UserDetailsImpl userDetails ) {
        Coffee coffee = coffeeRespoistory.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 커피입니다."));

        System.out.println("coffee 검색");
        Review review = new Review(reviewRequestDto.getStar(),
                reviewRequestDto.getReview(),
                coffee,
                userDetails.getUser());


        System.out.println("review 생성");
        reviewRepository.save(review);

        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(review.getId())
                .review(review.getReview())
                .build();

        System.out.println("코멘트 성공");
        return reviewResponseDto;
    }

    //댓글 조회
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

