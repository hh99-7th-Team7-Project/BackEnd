package com.sparta.coffang.report;

import com.sparta.coffang.model.*;
import com.sparta.coffang.report.requestDto.ChatPostReportDto;
import com.sparta.coffang.report.requestDto.CoffeeReviewReportDto;
import com.sparta.coffang.report.requestDto.PostCommentReportDto;
import com.sparta.coffang.report.requestDto.PostReportDto;
import com.sparta.coffang.repository.*;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReviewRepository reviewRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ChatPostRepository chatPostRepository;

    //커피 댓글 신고
    public ResponseEntity coffeeReviewReport(CoffeeReviewReportDto reviewReportDto, UserDetailsImpl userDetails) {
        //reviewId로 커피 코멘트 존재하는지
        Review review = reviewRepository.findById(reviewReportDto.getCoffeeReviewId()).orElseThrow(
                () -> new NullPointerException("해당하는 커피 댓글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        Report report = new Report(reviewReportDto);
        reportRepository.save(report);
        return ResponseEntity.ok().body("커피댓글 신고 성공하였습니다");
    }

    //게시글 신고
    public ResponseEntity postReport(PostReportDto postReportDto, UserDetailsImpl userDetails) {
        //게시글 존재하는지
        Post post = postRepository.findById(postReportDto.getPostId()).orElseThrow(
                () -> new NullPointerException("해당하는 게시글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        Report report = new Report(postReportDto);
        reportRepository.save(report);
        return ResponseEntity.ok().body("게시글 신고 성공하였습니다");
    }

    public ResponseEntity postCommentReport(PostCommentReportDto postCommentReportDto, UserDetailsImpl userDetails) {
        //게시글 댓글 존재하는지
        Comment comment = commentRepository.findById(postCommentReportDto.getPostId()).orElseThrow(
                () -> new NullPointerException("해당하는 게시글 댓글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        Report report = new Report(postCommentReportDto);
        reportRepository.save(report);
        return ResponseEntity.ok().body("게시글 댓글 신고 성공하였습니다");
    }

    public ResponseEntity chatPostReport(ChatPostReportDto chatPostReportDto, UserDetailsImpl userDetails) {
        //채팅방 존재하는지
        ChatPost chatPost = chatPostRepository.findById(chatPostReportDto.getChatPostId()).orElseThrow(
                () -> new NullPointerException("해당하는 채팅게시글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        Report report = new Report(chatPostReportDto);
        reportRepository.save(report);
        return ResponseEntity.ok().body("채팅 게시글 신고 성공하였습니다");
    }
}