package com.sparta.coffang.dto.responseDto;

import com.sparta.coffang.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialUserInfoDto {
    private String socialId;
    private String nickname;
    private String email;
    private String profileImage;

    public SocialUserInfoDto(User user) {
        this.socialId = user.getSocialId();
        this.nickname = user.getNickname();
        this.email = user.getUsername();
        this.profileImage = user.getProfileImage();
    }
}
