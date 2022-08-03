package com.sparta.coffang.dto.chatMessageDto;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatPostRequestDto {
    private String title;
    private String contents;
    private String calendar;
    private String map;
    private String meettime;
    private int totalcount;
}
