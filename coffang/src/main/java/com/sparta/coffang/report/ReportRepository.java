package com.sparta.coffang.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    //신고 당한 한명의 유저 거 다 찾기
    List<Report> findAllByUserId(Long userId);

    //커피댓글 신고를 유저가 했는지 안 했는지 유무
    Boolean existsByUserIdAndCoffeeReviewId(Long userId, Long coffeeReviewId);
    //신고한 유저와 커피댓글 ID로 일치하는 Report 들고오기
    Report findByUserIdAndCoffeeReviewId(Long userId, Long coffeeReviewId);

    //게시글 신고를 유저가 했는지 안 했는지 유무
    Boolean existsByUserIdAndPostId(Long userId, Long postId);
    //신고한 유저와 게시글 Id로 일치하는 Report 들고오기
    Report findByUserIdAndPostId(Long userId, Long postId);

    //게시글 댓글 신고를 유저가 했는지 안 했는지 유무
    Boolean existsByUserIdAndPostIdAndPostCommentId(Long userId, Long postId, Long postCommentId);
    //신고한 유저와 게시글 댓글 Id로 일치하는 Report 들고오기
    Report findByUserIdAndPostIdAndPostCommentId(Long userId, Long postId, Long postCommentId);

    //채팅 게시글 신고를 유저가 했는지 안 했는지 유무
    Boolean existsByUserIdAndChatPostId(Long userId, Long chatPostId);
    //신고한 유저와 채팅 게시글 Id로 일치하는 Report 들고오기
    Report findByUserIdAndChatPostId(Long userId, Long chatPostId);
}
