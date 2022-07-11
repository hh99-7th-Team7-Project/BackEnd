package com.sparta.coffang.dto.responseDto;

import com.sparta.coffang.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MyBoardResponseDto {

    private Long boardId;

    private String title;

    private String content;

    private String category;

    private LocalDateTime createdAt;

    private String nickname;

//    private Long totalComment;

//    private Long totalLove;
}
