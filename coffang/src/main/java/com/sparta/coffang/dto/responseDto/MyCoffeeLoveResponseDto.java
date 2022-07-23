package com.sparta.coffang.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class MyCoffeeLoveResponseDto {
    private Long id; // 커피아이디
    private String nickname;
    private String name;
    private String img;
    private String brand;
    private String category;
    private Long star;
}
