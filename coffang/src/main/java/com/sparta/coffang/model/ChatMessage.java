package com.sparta.coffang.model;

import com.sparta.coffang.dto.chatMessageDto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ChatMessage extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    @Column(nullable = false)
    private Long id;

    @Column
    private String message;

    @Column(nullable = false)
    private String senderName;

    @Column
    private String opposingUserName;

    @Column
    private String profileImage;


    @ManyToOne
    @JoinColumn(name = "CHATROOM_ID")
    private ChatRoom chatRoom;

    public ChatMessage(Long uid, ChatMessageDto chatMessageDto, ChatRoom chatRoom) {
        this.id= uid;
        this.message=chatMessageDto.getMessage();
        this.senderName=chatMessageDto.getSenderName();
        this.opposingUserName=chatMessageDto.getOpposingUserName();
        this.chatRoom=chatRoom;
        this.profileImage=chatMessageDto.getProfileImage();
    }



}
