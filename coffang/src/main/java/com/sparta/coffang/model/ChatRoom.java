package com.sparta.coffang.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ChatRoom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @Column(nullable = false)
    private String area;

    @Column
    private Long chatpostId;

    @Column
    private Long id;

    public ChatRoom(String area) {
        this.area = area;
    }

    public ChatRoom(String area, Long chatpostId) {
        this.area = area;
        this.chatpostId = chatpostId;
    }

}
