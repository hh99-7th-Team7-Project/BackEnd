package com.sparta.coffang.dto.requestDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRequestDto {

    private boolean admin = false;

    private String adminToken = "";
}
