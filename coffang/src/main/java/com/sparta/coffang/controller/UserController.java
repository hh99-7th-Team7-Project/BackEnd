package com.sparta.coffang.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.AdminRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.KakaoUserService;
import com.sparta.coffang.service.S3Service;
import com.sparta.coffang.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final KakaoUserService kakaoUserService;

    private final S3Service s3Service;

    //회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signupUser(@Valid @RequestPart("signup") SignupRequestDto requestDto,
                                     @RequestPart("profileImage") List<MultipartFile> profileImage) {

        try { // 회원가입 진행 성공시
            List<PhotoDto> photoDtos = s3Service.uploadFile(profileImage);
            return userService.signupUser(requestDto, photoDtos.get(0));
        }catch (Exception e){ // 에러나면
            throw new CustomException(ErrorCode.INVALID_LOGIN_ATTEMPT);
        }

    }

    //username 중복체크
    @PostMapping("/api/signup/checkID")
    public ResponseEntity checkUsername(@RequestBody SignupRequestDto requestDto) {
        return userService.checkUsername(requestDto);
    }

    //nickname 중복체크
    @PostMapping("/api/signup/nickID")
    public ResponseEntity checkNickname(@RequestBody SignupRequestDto requestDto) {
        return userService.checkNickname(requestDto);
    }

    //로그인 후 관리자 권한 얻을 수 있게 해보자
    @PutMapping("/api/signup/admin")
    public ResponseEntity adminAuthorization(@RequestBody AdminRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.adminAuthorization(requestDto, userDetails);
    }



    @GetMapping("/oauth/kakao/callback")
    public ResponseEntity kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        try { // 회원가입 진행 성공시
            kakaoUserService.kakaoLogin(code, response);
            return new ResponseEntity("카카오 로그인 성공", HttpStatus.OK);
        }catch (Exception e){ // 에러나면 false
            throw new CustomException(ErrorCode.INVALID_KAKAO_LOGIN_ATTEMPT);
        }

    }
}
