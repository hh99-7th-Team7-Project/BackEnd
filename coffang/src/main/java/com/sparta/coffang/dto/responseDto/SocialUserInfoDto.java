package com.sparta.coffang.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialUserInfoDto {
    private String socialId;
    private String nickname;
    private String email;

}
