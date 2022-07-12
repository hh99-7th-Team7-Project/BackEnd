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

    private Long loveId;

    private String nickname;

    private String coffeeName;

    private String img;

    private String brand;

    private String category;
}
