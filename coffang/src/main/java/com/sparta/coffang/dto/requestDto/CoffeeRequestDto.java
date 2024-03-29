package com.sparta.coffang.dto.requestDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeRequestDto {
    private String name;

    private List<String> size;

    private List<Long> price;

    private String category;
}
