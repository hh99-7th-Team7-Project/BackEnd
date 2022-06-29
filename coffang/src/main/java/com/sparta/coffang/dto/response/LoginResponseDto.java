package com.sparta.coffang.dto.response;

import lombok.Getter;

@Getter
public class LoginResponseDto {
    //로그인 시 body로 내려가는 사용자 정보

    private String nickname;

    private boolean login;

    private String accessToken;

    public LoginResponseDto(String nickname, boolean login, String accessToken) {
        this.nickname = nickname;
        this.login = login;  //login true/ false 상황
        this.accessToken = accessToken;
    }
}
