package com.sparta.coffang.dto.requestDto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@Data
public class SignupRequestDto {

    private String username;

    private String nickname;

    private String password;

    private boolean admin = false;
}
