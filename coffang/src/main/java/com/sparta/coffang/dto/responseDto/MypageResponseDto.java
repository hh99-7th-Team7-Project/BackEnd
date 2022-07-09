package com.sparta.coffang.dto.responseDto;

import com.sparta.coffang.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MypageResponseDto {
    private Long userId;

    private String username;

    private String nickname;

    private String profileImage;

    public MypageResponseDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.profileImage = user.getProfileImage();
    }
}
