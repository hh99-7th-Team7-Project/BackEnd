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

    //내가 쓴 게시글 (Post)
    @GetMapping("/mypage/myboard/{userId}")
    public ResponseEntity getMyBoard(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyBoard(userId, userDetails);
    }

    //내가 북마크한 커피 (Love)
    @GetMapping("/mypage/coffee/like/{userId}")
    public ResponseEntity getLikeCoffee(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getLikeCoffee(userId, userDetails);
    }

    //내가 북마크한 게시글 (BookMark)
    @GetMapping("/mypage/posts/bookmarks/{userId}")
    public ResponseEntity getBookMarkPost(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getBookMarkPost(userId, userDetails);
    }

    //내가 쓴 글 갯수
    @GetMapping("/mypage/myboards/{userId}")
    public ResponseEntity getMyBoardNum(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyBoardNum(userId, userDetails);
    }

    //내가 참여한 모임수
    @GetMapping("/mypage/myChat/{userId}")
    public ResponseEntity getMyChatNum(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyChatNum(userId, userDetails);
    }

    //내가 참가한 채팅방
    @GetMapping("/mypage/myChatRoom/{userId}")
    public ResponseEntity getMyChatRoom(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getMyChatRoom(userId, userDetails);
    }

    //신고당한 횟수 10번 이상 시 경고하기
    @GetMapping("/mypage/report/{userId}")
    public ResponseEntity getUserReport(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return mypageService.getUserReport(userId, userDetails);
    }
}
