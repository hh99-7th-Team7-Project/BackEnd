package com.sparta.coffang.report;

import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    //커피 댓글 신고
    @PostMapping("/reports/coffees/comments/{reviewId}")
    public ResponseEntity coffeeReviewReport(@PathVariable Long reviewId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.coffeeReviewReport(reviewId, userDetails);
    }

    //게시글 신고


    //게시글 댓글 신고


    //채팅방 신고
}
