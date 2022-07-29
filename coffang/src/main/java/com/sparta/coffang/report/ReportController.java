package com.sparta.coffang.report;

import com.sparta.coffang.report.requestDto.ChatPostReportDto;
import com.sparta.coffang.report.requestDto.CoffeeReviewReportDto;
import com.sparta.coffang.report.requestDto.PostCommentReportDto;
import com.sparta.coffang.report.requestDto.PostReportDto;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    //커피 댓글 신고
    @PostMapping("/reports/coffees/reviews/{userId}")
    public ResponseEntity coffeeReviewReport(@PathVariable Long userId, @RequestBody CoffeeReviewReportDto reviewReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.coffeeReviewReport(userId, reviewReportDto, userDetails);
    }

    //게시글 신고
    @PostMapping("/reports/posts/{userId}")
    public ResponseEntity postReport(@PathVariable Long userId, @RequestBody PostReportDto postReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postReport(userId, postReportDto, userDetails);
    }

    //게시글 댓글 신고
    @PostMapping("/reports/posts/comments/{userId}")
    public ResponseEntity postCommentReport(@PathVariable Long userId, @RequestBody PostCommentReportDto postCommentReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postCommentReport(userId, postCommentReportDto, userDetails);
    }

    //채팅방 신고
    @PostMapping("/reports/chatposts/{userId}")
    public ResponseEntity chatPostReport(@PathVariable Long userId, @RequestBody ChatPostReportDto chatPostReportDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.chatPostReport(userId, chatPostReportDto, userDetails);
    }
}
