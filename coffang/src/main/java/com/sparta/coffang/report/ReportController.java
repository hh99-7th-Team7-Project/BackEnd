package com.sparta.coffang.report;

import com.sparta.coffang.report.requestDto.*;
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
    public ResponseEntity coffeeReviewReport(@PathVariable Long userId, @RequestBody ReportRequestDto reportRequestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.coffeeReviewReport(userId, reportRequestDto, userDetails);
    }

    //게시글 신고
    @PostMapping("/reports/posts/{userId}")
    public ResponseEntity postReport(@PathVariable Long userId, @RequestBody ReportRequestDto reportRequestDto,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postReport(userId, reportRequestDto, userDetails);
    }

    //게시글 댓글 신고
    @PostMapping("/reports/posts/comments/{userId}")
    public ResponseEntity postCommentReport(@PathVariable Long userId, @RequestBody ReportRequestDto reportRequestDto,
                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.postCommentReport(userId, reportRequestDto, userDetails);
    }

    //채팅방 신고
    @PostMapping("/reports/chatposts/{userId}")
    public ResponseEntity chatPostReport(@PathVariable Long userId, @RequestBody ReportRequestDto reportRequestDto,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reportService.chatPostReport(userId, reportRequestDto, userDetails);
    }
}
