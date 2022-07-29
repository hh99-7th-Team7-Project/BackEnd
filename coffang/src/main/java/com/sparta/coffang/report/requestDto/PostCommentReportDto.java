package com.sparta.coffang.report.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentReportDto {
    private Long userId;
    private Long postId;
    private Long postCommentId;
}
