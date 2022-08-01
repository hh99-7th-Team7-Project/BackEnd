package com.sparta.coffang.dto.chatMessageDto;


import com.sparta.coffang.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPostMemberDto {
    private String userTitle;
    private String userProfile;
    private Long id;

    public ChatPostMemberDto(User user) {
        this.userTitle= user.getNickname();
        this.userProfile = user.getProfileImage();
        this.id = user.getId();
    }
}
