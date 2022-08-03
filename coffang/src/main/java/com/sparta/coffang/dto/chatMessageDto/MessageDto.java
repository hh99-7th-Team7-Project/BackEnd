package com.sparta.coffang.dto.chatMessageDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class MessageDto {

    private String username;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

}

