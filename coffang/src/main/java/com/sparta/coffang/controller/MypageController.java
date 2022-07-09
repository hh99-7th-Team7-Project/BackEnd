package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.dto.responseDto.MypageResponseDto;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.MypageService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;


    //유저 이미지 프로필 변경 /formdata형식
    @PutMapping("/mypage/userInfo/image/{userId}")
    public ResponseEntity updateUserImage (@PathVariable Long userId,
                                           @RequestPart("profileImage") List<MultipartFile> profileImages,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.updateUserImage(userId, profileImages, userDetails);
    }

    //유저 name 프로필 변경 /JSON형식
    @PutMapping("/mypage/userInfo/name/{userId}")
    public ResponseEntity updateUserName (@PathVariable Long userId,
                                          @RequestBody MypageRequestDto requestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.updateUserName(userId, requestDto, userDetails);
    }

    @GetMapping("/mypage/userInfo/{userId}")
    public MypageResponseDto getUserProfile() {

        return null;
    }
}
