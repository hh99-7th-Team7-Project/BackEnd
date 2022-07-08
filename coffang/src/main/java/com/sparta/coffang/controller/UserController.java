package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.AdminRequestDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.UserRoleEnum;
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
                                     @RequestPart("profileImage") List<MultipartFile> profileImage) {
        List<PhotoDto> photoDtos = s3Service.uploadFile(profileImage);
        return userService.signupUser(requestDto, photoDtos.get(0));
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

    //test 커피 이미지 등록하기
    @PostMapping("/coffee/image")
    public ResponseEntity imageUpload(@RequestPart("imgUrl") List<MultipartFile> multipartFiles,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {

        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        List<PhotoDto> photoDtos = s3Service.uploadFile(multipartFiles);
        return userService.imageUpload(photoDtos.get(0));
    }
    //test 커피 이미지 하나 가져오기
    @GetMapping("/coffee/image/{imageId}")
    public ResponseEntity getImage(@PathVariable Long imageId) {
        return userService.getImage(imageId);
    }
}
