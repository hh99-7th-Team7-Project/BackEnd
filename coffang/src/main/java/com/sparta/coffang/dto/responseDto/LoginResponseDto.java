package com.sparta.coffang.dto.responseDto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    //로그인 시 body로 내려가는 사용자 정보

    private Long userId;

    private String nickname;

    private boolean login;

    private String accessToken;

    private String profileImage;

    //일반 로그인할 때 프론트에 내려주는 값
    public LoginResponseDto(Long userId, String nickname, boolean login, String accessToken, String profileImage) {
        this.userId = userId;
        this.nickname = nickname;
        this.login = login;  //login true/ false 상황
        this.accessToken = accessToken;
        this.profileImage = profileImage;
    }
}
