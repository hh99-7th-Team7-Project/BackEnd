package com.sparta.coffang.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailRequestDto {
    private String sender;

    private String address;

    private String title;

    private String content;
}
