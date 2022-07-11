package com.sparta.coffang.model;

import com.sparta.coffang.dto.requestDto.CommentRequestDto;
import com.sparta.coffang.dto.requestDto.ReviewRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column(nullable = false)
    private String comment;// 리뷰


    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Comment(String comment , Post post, User user) {

        this.comment = comment;
        this.post = post;
        this.user = user;}

//    }

    //리뷰수정
    public void update(CommentRequestDto commentRequestDto) {
        this.comment = commentRequestDto.getComment();
    }
}

