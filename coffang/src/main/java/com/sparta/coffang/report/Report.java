package com.sparta.coffang.report;

import com.sparta.coffang.report.requestDto.*;
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

    @Column //신고받은 커피댓글, 게시글, 게시글 댓글, 채팅게시글 각 고유한 ID
    private Long reportId;

    @Column //신고한 유저
    private Long userId;

    //커피 댓글 신고
    public Report(String category, ReportRequestDto reportRequestDto, Long userId) {
        this.category = category;
        this.reportUserId = reportRequestDto.getUserId();
        this.reportId = reportRequestDto.getReportId();
        this.userId = userId;
    }
}
