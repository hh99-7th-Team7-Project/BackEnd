package com.sparta.coffang.dto.chatMessageDto;

import com.sparta.coffang.model.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ChatMessagedResponseDto {

    private String senderName;
    private String message;
    private String opposingUserName;
    private LocalDateTime createdAt;
    private String profileImage;

    public ChatMessagedResponseDto (ChatMessage chatMessage) {
        this.senderName = chatMessage.getSenderName();
        this.message = chatMessage.getMessage();
        this.opposingUserName = chatMessage.getOpposingUserName();
        this.createdAt = chatMessage.getCreatedAt();
        this.profileImage=chatMessage.getProfileImage();
    }


}
