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


    //유저 이미지 프로필 변경 /formdata형식
    @PutMapping("/mypage/userInfo/image/{userId}")
    public ResponseEntity updateUserImage (@PathVariable Long userId,
                                           @RequestPart("profileImage") List<MultipartFile> profileImages,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String defaultImg = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/fbcebde7-ae14-42f0-9a75-261914c1053f.png";
        String image = "";
        // 이미지를 안 넣으면 기본이미지 주기
        if(profileImages.get(0).isEmpty()) { //이미지가 안들어오면 true
            image = defaultImg;
        } else {  //profileImages에 유저가 등록한 이미지가 들어올 때
            List<PhotoDto> photoDtos = s3Service.uploadFile(profileImages);
            image = photoDtos.get(0).getPath();
        }

        return mypageService.updateUserImage(userId, defaultImg, image, userDetails);
    }

    //유저 name 프로필 변경 /JSON형식
    @PutMapping("/mypage/userInfo/name/{userId}")
    public ResponseEntity updateUserName (@PathVariable Long userId,
                                          @RequestBody MypageRequestDto requestDto,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.updateUserName(userId, requestDto, userDetails);
    }

    // 유저 정보 조회 (username, nickname, profileImage)
    @GetMapping("/mypage/userInfo/{userId}")
    public ResponseEntity getUserInfo(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return mypageService.getUserInfo(userId, userDetails);
    }
}
