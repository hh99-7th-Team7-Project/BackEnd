package com.sparta.coffang.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    //신고 당한 한명의 유저 다 찾기
    List<Report> findAllByReportUserId(Long reportUserId);

    //신고를 유저가 했는지 안 했는지 유무
    Boolean existsByUserIdAndReportIdAndCategory(Long userId, Long reportId, String category);
    //신고한 유저와 커피댓글,게시글,게시글댓글,채팅게시글 ID로 일치하는 Report 들고오기
    Report findByUserIdAndReportIdAndCategory(Long userId, Long reportId, String category);
}
