package com.sparta.coffang.dto.chatMessageDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.coffang.model.ChatPost;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatPostResponseDto {

    private Long userId;
    private Long chatpostId;
    private String nickname;
    private String title;
    private String contents;
    private String calendar;
    private String map;
    private String meettime;
    private int totalcount;
    private int count;
    private boolean completed;
    private String beforeTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public ChatPostResponseDto(ChatPost chatPost, boolean completed, String beforeTime) {
        this.chatpostId = chatPost.getChatpostId();
        this.userId = chatPost.getUser().getId();
        this.nickname = chatPost.getUser().getNickname();
        this.title = chatPost.getTitle();
        this.contents = chatPost.getContents();
        this.calendar = chatPost.getCalendar();
        this.map = chatPost.getMap();
        this.meettime = chatPost.getMeettime();
        this.totalcount = chatPost.getTotalcount();
        this.count = chatPost.getCount();
        this.completed = completed;
        this.beforeTime = beforeTime;
    }

//    public ChatPostResponseDto(ChatPost chatPost, boolean completed, String beforeTime) {
//        this.chatpostId = chatPost.getChatpostId();
//        this.userId = chatPost.getUser().getId();
//        this.nickname = chatPost.getUser().getNickname();
//        this.title = chatPost.getTitle();
//        this.contents = chatPost.getContents();
//        this.calendar = chatPost.getCalendar();
//        this.map = chatPost.getMap();
//        this.meettime = chatPost.getMeettime();
//        this.totalcount = chatPost.getTotalcount();
//        this.count = chatPost.getCount();
//        this.completed = completed;
//        this.beforeTime = beforeTime;
//    }
}






