package com.sparta.coffang.dto.responseDto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    //로그인 시 body로 내려가는 사용자 정보

    private String nickname;

    private boolean login;

    private String accessToken;

    private String profileImage;

    public LoginResponseDto(String nickname, boolean login, String accessToken, String profileImage) {
        this.nickname = nickname;
        this.login = login;  //login true/ false 상황
        this.accessToken = accessToken;
        this.profileImage = profileImage;
    }
}
