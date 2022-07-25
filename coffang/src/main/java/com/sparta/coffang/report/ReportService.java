package com.sparta.coffang.report;

import com.sparta.coffang.model.Review;
import com.sparta.coffang.repository.ReviewRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReviewRepository reviewRepository;

    //커피 댓글 신고
    public ResponseEntity coffeeReviewReport(Long commentId, UserDetailsImpl userDetails) {
        //commentId로 커피 코멘트 찾기
        Review review = reviewRepository.findById(commentId).orElseThrow(
                () -> new NullPointerException("해당하는 커피 댓글이 없습니다")
        );


    }
}
