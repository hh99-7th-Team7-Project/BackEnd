package com.sparta.coffang.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BookMark {
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    Long bookMarkId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private  Post post;




    public BookMark(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}

