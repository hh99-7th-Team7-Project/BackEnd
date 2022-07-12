package com.sparta.coffang.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
        private Long id;
        private String comment;
        private String nickname;
        private LocalDateTime createdAt;
}
