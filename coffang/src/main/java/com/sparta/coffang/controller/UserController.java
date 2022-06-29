package com.sparta.coffang.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.coffang.dto.request.AdminRequestDto;
import com.sparta.coffang.dto.response.SignupRequestDto;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.KakaoUserService;
import com.sparta.coffang.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final KakaoUserService kakaoUserService;

    //회원가입
    @PostMapping("/api/signup")
    public String signupUser(@Valid @RequestBody SignupRequestDto requestDto) {

        try { // 회원가입 진행 성공시 true
            return userService.signupUser(requestDto);
        }catch (Exception e){ // 에러나면 false
            return "회원가입 실패";
        }

    }

    //username 중복체크
    @PostMapping("/api/signup/checkID")
    public boolean checkUsername(@RequestBody SignupRequestDto requestDto) {
        return userService.checkUsername(requestDto);
    }

    //nickname 중복체크
    @PostMapping("/api/signup/nickID")
    public boolean checkNickname(@RequestBody SignupRequestDto requestDto) {
        return userService.checkNickname(requestDto);
    }

    //로그인 후 관리자 권한 얻을 수 있게 해보자
    @PutMapping("/api/signup/admin")
    public UserRoleEnum adminAuthorization(@RequestBody AdminRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.adminAuthorization(requestDto, userDetails);
    }



    @GetMapping("/oauth/kakao/callback")
    public boolean kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        try { // 회원가입 진행 성공시 true
            System.out.println("카카오톡 로그인 시도");  //#
            kakaoUserService.kakaoLogin(code, response);
            System.out.println("로그인 성공");  //#
            return true;
        }catch (Exception e){ // 에러나면 false
            System.out.println("카톡 로그인 실패!");
            return false;
        }

    }
}