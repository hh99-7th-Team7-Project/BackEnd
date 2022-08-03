package com.sparta.coffang.model;


import com.sparta.coffang.dto.chatMessageDto.ChatPostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ChatPost extends Timestamped{

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatpostId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String calendar;

    @Column(nullable = false)
    private String map;

    @Column(nullable = false)
    private String meettime;

    @Column(nullable = false)
    private int totalcount;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private boolean completed;

    public ChatPost(ChatPostRequestDto chatPostRequestDto, int count, boolean completed, User user) {
        this.title = chatPostRequestDto.getTitle();
        this.contents = chatPostRequestDto.getContents();
        this.calendar = chatPostRequestDto.getCalendar();
        this.map = chatPostRequestDto.getMap();
        this.meettime = chatPostRequestDto.getMeettime();
        this.totalcount = chatPostRequestDto.getTotalcount();
        this.count = count;
        this.completed = completed;
        this.user = user;
    }


    public void update(String title, String contents, String calendar, String map,String meettime, int totalcount, boolean completed) {
        this.title = title;
        this.contents = contents;
        this.calendar = calendar;
        this.map = map;
        this.meettime = meettime;
        this.totalcount = totalcount;
        this.completed = completed;
    }

    public void updateCount(int result) {
        this.count = result;
    }
}
