package com.sparta.coffang.model;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@NoArgsConstructor
@Entity
@Getter
public class Love {

    //좋아요 Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    //사용자 -> 삭제 시 아무나 삭제 못해야 하며 좋아요 눌렀던 user만 좋아요 추가/삭제 가능
    @Column(nullable = false)
    private Long userId;

    //게시글 -> 좋아요 해당 게시글에만 저장해야하기 때문에
    @Column(nullable = false)
    private Long postId;

    //좋아요
    @Column(nullable = false)
    private Boolean is_Check;

    //@Column(
    public Likes(Long postId, LikeDto likeDto){
        this.postId = postId;
        this.userId = likeDto.getUserId();
        this.is_Check = likeDto.getIs_Check();
    }

    public void update(LikeDto likeDto){

        this.is_Check = likeDto.getIs_Check();
    }
}