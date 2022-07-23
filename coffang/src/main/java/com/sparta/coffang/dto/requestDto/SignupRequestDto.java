package com.sparta.coffang.dto.requestDto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class SignupRequestDto {

    private String username;

    private String nickname;

    private String password;
}
