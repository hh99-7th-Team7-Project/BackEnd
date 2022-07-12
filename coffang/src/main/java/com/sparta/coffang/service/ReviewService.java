package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.ReviewRequestDto;
import com.sparta.coffang.dto.responseDto.ReviewResponseDto;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.ReviewRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createReview(ReviewRequestDto reviewRequestDto, Long id, UserDetailsImpl userDetails ) {
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
                .star(review.getStar())
                .nickname(review.getUser().getNickname())
                .build();

        System.out.println("코멘트 성공");
        return ResponseEntity.ok().body(reviewResponseDto);
    }

    //댓글 조회
    public ResponseEntity findReviews(Long id) {
        List<Review> reviews = reviewRepository.findAllByCoffeeId(id);
        List<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();

        for (Review review : reviews) {
            ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                    .id(review.getId())
                    .review(review.getReview())
                    .star(review.getStar())
                    .nickname(review.getUser().getNickname())
                    .build();

            reviewResponseDtos.add(reviewResponseDto);

        }
        System.out.println("코멘트 검색 성공");
        return ResponseEntity.ok().body(reviewResponseDtos);
    }

    //삭제
    public ResponseEntity deleteReview(Long reviewId, UserDetailsImpl userDetails) {
        //삭제할 댓글이 있는지 확인
        Review review = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        //삭제할 댓글의 유저와 현재 로그인한 유저가 같은지 확인
        if(!review.getUser().getNickname().equals(userDetails.getUser().getNickname())){
            throw new IllegalArgumentException("자신의 리뷰만 삭제할 수 있습니다.");
        }

        System.out.println("delete reviewId : " + reviewId);
        reviewRepository.deleteById(reviewId);
        System.out.println("코멘트 삭제 성공");
        return ResponseEntity.noContent().build();
    }

    //댓글 수정
    @Transactional
    public ResponseEntity updateReview(Long reviewId, ReviewRequestDto reviewRequestDto, UserDetailsImpl userDetails) {
        Review review =reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!review.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
            review.update(reviewRequestDto);
            throw new IllegalArgumentException("자신의 댓글만 수정할 수 있습니다.");
        }

        ReviewResponseDto reviewResponseDto = ReviewResponseDto.builder()
                .id(review.getId())
                .review(review.getReview())
                .star(review.getStar())
                .nickname(review.getUser().getNickname())
                .build();

        review.update(reviewRequestDto);
        return ResponseEntity.ok().body(reviewResponseDto);
    }
 }

