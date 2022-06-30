package com.sparta.coffang.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class SignupRequestDto {

    @Email
    @NotBlank
    private String username;

    @NotBlank
    private String nickname;

    @NotBlank
//    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{4,20}")
    @Pattern(regexp = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}")
    private String password;

    private boolean admin = false;
}