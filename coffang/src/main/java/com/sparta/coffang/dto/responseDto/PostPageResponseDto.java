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
public class PostPageResponseDto {
    private Long id;

    private String title;

    private String category;

    private String nickname;

    private boolean isNew;

    private LocalDateTime createdAt;

    private String userImg;

    private int view;

    private int totalComment;

//    private Long totalLove;
}
