package com.sparta.coffang.dto.requestDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class SignupImgRequestDto {

    private String username;

    private String nickname;

    private String password;

    private String profileImage;

    private boolean admin = false;
}
