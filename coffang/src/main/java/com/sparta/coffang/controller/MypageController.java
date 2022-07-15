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
    private final S3Service s3Service;

    //유저 프로필 변경
    @PutMapping("/mypage/userInfo/{userId}")
    public ResponseEntity updateUser (@PathVariable Long userId,
                                      @RequestBody MypageRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.updateUser(userId, requestDto, userDetails);
    }
    

    // 유저 정보 조회 (username, nickname, profileImage)
    @GetMapping("/mypage/userInfo/{userId}")
    public ResponseEntity getUserInfo(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.getUserInfo(userId, userDetails);
    }
}
