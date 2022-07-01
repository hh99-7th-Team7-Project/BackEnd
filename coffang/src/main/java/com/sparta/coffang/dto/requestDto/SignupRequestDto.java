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
