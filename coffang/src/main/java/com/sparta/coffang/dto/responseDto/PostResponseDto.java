package com.sparta.coffang.dto.responseDto;

import com.sparta.coffang.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PostResponseDto {
    private Long id;

    private String title;

    private String content;

    private String category;

    private Long userId;

    private String nickname;

    private String userImg;

    private LocalDateTime createdAt;

    private int view;

    private int totalComment;

    private int totalLove;

    private int totalPage;

    private boolean loveCheck;

    private boolean bookmark;

    private boolean isReport;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.title = post.getTitle();
        this.category = post.getCategory();
        this.nickname = post.getUser().getNickname();
        this.createdAt = post.getCreatedAt();
        this.userImg = post.getUser().getProfileImage();
        this.view = post.getView();
        this.totalComment = post.getComments().size();
        this.loveCheck = false;
        this.bookmark = false;
        this.isReport = false;
        this.userId = post.getUser().getId();

        if (post.getLoveList() != null)
            this.totalLove = post.getLoveList().size();
    }

    public void setLoveCheck(boolean loveCheck){
        this.loveCheck = loveCheck;
    }

    public void setBookmark(boolean bookmark){
        this.bookmark = bookmark;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setIsReport(boolean isReport) {
        this.isReport = isReport;
    }
}
