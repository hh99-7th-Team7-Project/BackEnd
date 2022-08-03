package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.AdminRequestDto;
import com.sparta.coffang.dto.requestDto.SignupImgRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    //회원가입
    @PostMapping("/api/signup")
    public ResponseEntity signupUser(@RequestPart("signup") SignupRequestDto requestDto,
                                     @RequestPart("profileImage") List<MultipartFile> profileImages) {
        String defaultImg = "https://mytest-coffick.s3.ap-northeast-2.amazonaws.com/coffindBasicImage.png"; // 기본이미지
        String image = "";
        // 이미지를 안 넣으면 기본이미지 주기
        if (profileImages.get(0).isEmpty()) { // 이미지가 안들어오면 true
            image = defaultImg;
        } else {  // profileImages에 유저가 등록한 이미지가 들어올 때
            List<PhotoDto> photoDtos = s3Service.uploadFile(profileImages);
            image = photoDtos.get(0).getPath();
        }

        return userService.signupUser(requestDto, image);
    }

    //회원가입에 이미지가 null이 들어올 때
    @PostMapping("/api/user/signup")
    public ResponseEntity signupNullUser(@RequestBody SignupImgRequestDto requestDto) {
        return userService.signupNullUser(requestDto);
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

    //로그인 후 관리자 권한 얻을 수 있는 API
    @PutMapping("/api/signup/admin")
    public ResponseEntity adminAuthorization(@RequestBody AdminRequestDto requestDto,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.adminAuthorization(requestDto, userDetails);
    }

    //소셜로그인 사용자 정보 조회
    @GetMapping("/social/user/islogin")
    public ResponseEntity socialUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.socialUserInfo(userDetails);
    }

}
