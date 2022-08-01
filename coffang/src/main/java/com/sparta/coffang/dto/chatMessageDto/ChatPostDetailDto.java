package com.sparta.coffang.dto.chatMessageDto;

import com.sparta.coffang.model.ChatPost;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatPostDetailDto {

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
    private List<ChatPostMemberDto> chatPostMember;

    public ChatPostDetailDto(ChatPost chatPost, List<ChatPostMemberDto> chatPostMemberDto, boolean completed, String beforeTime) {
        this.chatpostId = chatPost.getChatpostId();
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
        this.chatPostMember = chatPostMemberDto;
    }

    public ChatPostDetailDto(ChatPost chatPost, List<ChatPostMemberDto> chatPostMemberDto, String beforeTime) {
        this.chatpostId = chatPost.getChatpostId();
        this.nickname = chatPost.getUser().getNickname();
        this.title = chatPost.getTitle();
        this.contents = chatPost.getContents();
        this.calendar = chatPost.getCalendar();
        this.map = chatPost.getMap();
        this.meettime = chatPost.getMeettime();
        this.totalcount = chatPost.getTotalcount();
        this.count = chatPost.getCount();
        this.beforeTime = beforeTime;
        this.chatPostMember = chatPostMemberDto;
    }


}
