package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.ReviewRequestDto;
import com.sparta.coffang.dto.responseDto.ReviewResponseDto;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    //댓글수정
    @PutMapping("coffee/{brand}/{id}/review/{reviewid}")
    public Long updateReview(@PathVariable Long reviewid, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.updateReview(reviewid, reviewRequestDto);
        return reviewid;
    }
    //댓글삭제
    @DeleteMapping("coffee/{brand}/{id}/review/{reviewid}")
    public Long deleteReview(@PathVariable("reviewid") Long reviewId) {
        reviewService.deleteReview(reviewId);

        return reviewId;
    }

    //댓글등록
    @PostMapping("coffee/{brand}/{id}/review")
    public ReviewResponseDto createReview(@RequestBody ReviewRequestDto reviewRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return reviewService.createReview(reviewRequestDto, id, userDetails);
    }
    //댓글조회
    @GetMapping("coffee/{brand}/{id}/review")
    public List<ReviewResponseDto> viewReview(@PathVariable Long id) {return reviewService.findReviews(id);}
}


