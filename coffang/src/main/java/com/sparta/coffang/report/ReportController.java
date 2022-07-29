package com.sparta.coffang.report;

import com.sparta.coffang.report.requestDto.ChatPostReportDto;
import com.sparta.coffang.report.requestDto.CoffeeReviewReportDto;
import com.sparta.coffang.report.requestDto.PostCommentReportDto;
import com.sparta.coffang.report.requestDto.PostReportDto;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    //커피 댓글 신고
    @PostMapping("/reports/coffees/reviews")
    public ResponseEntity coffeeReviewReport(@RequestBody CoffeeReviewReportDto reviewReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.coffeeReviewReport(reviewReportDto, userDetails);
    }

    //게시글 신고
    @PostMapping("/reports/posts")
    public ResponseEntity postReport(@RequestBody PostReportDto postReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postReport(postReportDto, userDetails);
    }

    //게시글 댓글 신고
    @PostMapping("/reports/posts/comments")
    public ResponseEntity postCommentReport(@RequestBody PostCommentReportDto postCommentReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postCommentReport(postCommentReportDto, userDetails);
    }

    //채팅방 신고
    @PostMapping("/reports/chatposts")
    public ResponseEntity chatPostReport(@RequestBody ChatPostReportDto chatPostReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.chatPostReport(chatPostReportDto, userDetails);
    }
}
