package com.sparta.coffang.report;

import com.sparta.coffang.model.*;
import com.sparta.coffang.report.requestDto.*;
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
    public ResponseEntity coffeeReviewReport(Long userId, ReportRequestDto reportRequestDto, UserDetailsImpl userDetails) {
        //reviewId로 커피 코멘트 존재하는지
        Review review = reviewRepository.findById(reportRequestDto.getReportId()).orElseThrow(
                () -> new NullPointerException("해당하는 커피 댓글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        String category = "커피댓글";
        //한 사람이 한번만 신고할 수 있게하기
        return getBooleanResponseEntity(userId, reportRequestDto, category);
    }

    //게시글 신고
    public ResponseEntity postReport(Long userId, ReportRequestDto reportRequestDto, UserDetailsImpl userDetails) {
        //게시글 존재하는지
        Post post = postRepository.findById(reportRequestDto.getReportId()).orElseThrow(
                () -> new NullPointerException("해당하는 게시글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        String category = "게시글";
        //한 사람이 한번만 신고할 수 있게하기
        return getBooleanResponseEntity(userId, reportRequestDto, category);
    }

    //게시글 댓글 신고
    public ResponseEntity postCommentReport(Long userId, ReportRequestDto reportRequestDto, UserDetailsImpl userDetails) {
        //게시글 댓글 존재하는지
        Comment comment = commentRepository.findById(reportRequestDto.getReportId()).orElseThrow(
                () -> new NullPointerException("해당하는 게시글 댓글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        String category = "게시글댓글";
        //한 사람이 한번만 신고할 수 있게하기
        return getBooleanResponseEntity(userId, reportRequestDto, category);
    }

    //채팅방 신고
    public ResponseEntity chatPostReport(Long userId, ReportRequestDto reportRequestDto, UserDetailsImpl userDetails) {
        //채팅방 존재하는지
        ChatPost chatPost = chatPostRepository.findById(reportRequestDto.getReportId()).orElseThrow(
                () -> new NullPointerException("해당하는 채팅 게시글이 없습니다")
        );
        //신고당한 유저가 존재하는지
        User user = userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new NullPointerException("해당하는 사용자가 없습니다")
        );

        String category = "채팅게시글";
        return getBooleanResponseEntity(userId, reportRequestDto, category);
    }

    //한 사람이 한번만 신고할 수 있게하기
    private ResponseEntity<Boolean> getBooleanResponseEntity(Long userId, ReportRequestDto reportRequestDto, String category) {
        if (reportRepository.existsByUserIdAndReportIdAndCategory(userId, reportRequestDto.getReportId(), category)) {
            Report existReport = reportRepository.findByUserIdAndReportIdAndCategory(userId, reportRequestDto.getReportId(), category);
            reportRepository.deleteById(existReport.getId());
            return ResponseEntity.ok().body(false);
        } else {
            Report report = new Report(category, reportRequestDto, userId);
            reportRepository.save(report);
            return ResponseEntity.ok().body(true);
        }
    }
}