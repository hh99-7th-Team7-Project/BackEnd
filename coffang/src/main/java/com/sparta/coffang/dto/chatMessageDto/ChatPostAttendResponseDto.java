package com.sparta.coffang.dto.chatMessageDto;

import lombok.Data;

@Data
public class ChatPostAttendResponseDto {

    private String result;
    public ChatPostAttendResponseDto(String msg) {
        this.result = msg;
    }
}
