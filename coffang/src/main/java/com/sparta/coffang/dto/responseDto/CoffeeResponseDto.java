package com.sparta.coffang.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoffeeResponseDto {
    private Long id;

    private String name;

    private List<Map<String, Object>> pricePair;

    private String img;

    private String brand;

    private String category;
}
