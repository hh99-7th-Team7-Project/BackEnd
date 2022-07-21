package com.sparta.coffang.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PostResponseDto {
    private Long id;

    private String title;

    private String content;

    private String category;

    private String img;

    private String nickname;

    private String userImg;

    private LocalDateTime localDateTime;

    private int view;
}
