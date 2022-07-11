package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.AdminRequestDto;
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

        //기본이미지
        String defaultImg = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/fbcebde7-ae14-42f0-9a75-261914c1053f.png";
        String image = "";
        // 이미지를 안 넣으면 기본이미지 주기
        if(profileImages.get(0).isEmpty()) { //이미지가 안들어오면 true
            image = defaultImg;
        } else {  //profileImages에 유저가 등록한 이미지가 들어올 때
            List<PhotoDto> photoDtos = s3Service.uploadFile(profileImages);
            image = photoDtos.get(0).getPath();
        }

        return userService.signupUser(requestDto, image);
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

}
