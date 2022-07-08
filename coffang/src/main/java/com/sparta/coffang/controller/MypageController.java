package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.dto.responseDto.MypageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
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
    public ResponseEntity updateUserProfile (@PathVariable Long userId,
                                             @RequestPart("updateProfile") MypageRequestDto requestDto,
                                             @RequestPart("profileImage") List<MultipartFile> profileImage,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if(profileImage == null) {
            //기본이미지
//            String profile = "https://ossack.s3.ap-northeast-2.amazonaws.com/basicprofile.png";
            String profileImage = "기본이미지 넣기";
        } else {

        }
        
        return mypageService.updateUserProfile(userId, requestDto, photoDtos.get(0), userDetails);
    }

    @GetMapping("/mypage/userInfo/{userId}")
    public MypageResponseDto getUserProfile() {


    }
}
