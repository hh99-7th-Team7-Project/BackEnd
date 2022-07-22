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
public class PostLove {
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    Long LoveId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private  Post post;




    public PostLove(User user, Post post) {
        this.user = user;
        this.post = post;
        this.post.getLoveList().add(this);


    }
}

