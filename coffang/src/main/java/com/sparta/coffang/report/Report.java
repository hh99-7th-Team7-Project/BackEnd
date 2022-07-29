package com.sparta.coffang.report;

import com.sparta.coffang.report.requestDto.ChatPostReportDto;
import com.sparta.coffang.report.requestDto.CoffeeReviewReportDto;
import com.sparta.coffang.report.requestDto.PostCommentReportDto;
import com.sparta.coffang.report.requestDto.PostReportDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column //신고당한 유저
    private Long reportUserId;

    @Column
    private Long coffeeId;

    @Column
    private Long coffeeReviewId;

    @Column
    private Long postId;

    @Column
    private Long postCommentId;

    @Column
    private Long chatPostId;

    @Column //신고한 유저
    private Long userId;

    //커피 댓글 신고
    public Report(CoffeeReviewReportDto coffeeReviewReportDto, Long userId) {
        this.category = "커피댓글";
        this.reportUserId = coffeeReviewReportDto.getUserId();
        this.coffeeId = coffeeReviewReportDto.getCoffeeId();
        this.coffeeReviewId = coffeeReviewReportDto.getCoffeeReviewId();
        this.userId = userId;
    }

    //게시글 신고
    public Report(PostReportDto postReportDto, Long userId) {
        this.category = "게시글";
        this.reportUserId = postReportDto.getUserId();
        this.postId = postReportDto.getPostId();
        this.userId = userId;
    }

    //게시글 댓글 신고
    public Report(PostCommentReportDto postCommentReportDto, Long userId) {
        this.category = "게시글댓글";
        this.reportUserId = postCommentReportDto.getUserId();
        this.postId = postCommentReportDto.getPostId();
        this.postCommentId = postCommentReportDto.getPostCommentId();
        this.userId = userId;
    }

    //채팅방 신고
    public Report(ChatPostReportDto chatPostReportDto, Long userId) {
        this.category = "채팅게시글";
        this.reportUserId = chatPostReportDto.getUserId();
        this.chatPostId = chatPostReportDto.getChatPostId();
        this.userId = userId;
    }
}
