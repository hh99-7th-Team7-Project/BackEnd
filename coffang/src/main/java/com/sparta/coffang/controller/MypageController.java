package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MypageService mypageService;

    //유저 프로필 변경
    @PutMapping("/mypage/userInfo/{userId}")
    public ResponseEntity updateUser (@PathVariable Long userId, @RequestBody MypageRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.updateUser(userId, requestDto, userDetails);
    }

    // 유저 정보 조회 (username, nickname, profileImage)
    @GetMapping("/mypage/userInfo/{userId}")
    public ResponseEntity getUserInfo(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getUserInfo(userId, userDetails);
    }

    //내가 쓴 게시글 (posts)
    @GetMapping("/mypage/myboard/{userId}")
    public ResponseEntity getMyBoard(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyBoard(userId, userDetails);
    }

    //내가 북마크한 커피
    @GetMapping("/mypage/coffee/like/{userId}")
    public ResponseEntity getLikeCoffee(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getLikeCoffee(userId, userDetails);
    }

    //내가 북마크한 게시글 - 구현중
//    @GetMapping("/mypage/board/like/{userId}")
//    public ResponseEntity getLikeBoard(@PathVariable Long userId,
//                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
//
//        return mypageService.getLikeBoard(userId, userDetails);
//    }
}
