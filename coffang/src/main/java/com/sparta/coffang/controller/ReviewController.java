package com.sparta.coffang.controller;

import com.sparta.coffang.dto.ReviewRequestDto;
import com.sparta.coffang.dto.ReviewResponseDto;
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
    public Long updateComment(@PathVariable Long reviewid, @RequestBody ReviewRequestDto reviewRequestDto) {
        Review review = reviewService.updateReview(reviewid, reviewRequestDto);
        return review.getId();
    }
    //댓글삭제
    @DeleteMapping("coffee/{brand}/{id}/review/{reviewid}")
    public Long deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteComment(reviewId);

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


