package com.sparta.coffang.dto.requestDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeRequestDto {
    private Long id;

    private String name;

    private List<String> size;

    private List<Long> price;

    private String img;

    private String category;
}
