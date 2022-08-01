package com.sparta.coffang.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Attend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attendId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long chatpostId;

    public Attend(Long userId, Long chatpostId) {
        this.userId = userId;
        this.chatpostId = chatpostId;
    }



}
