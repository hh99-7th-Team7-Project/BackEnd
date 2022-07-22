package com.sparta.coffang.controller;

import com.sparta.coffang.dto.chatMessageDto.*;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.ChatPostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Getter
@RestController
@RequiredArgsConstructor
public class ChatPostController {
    private final ChatPostService chatPostService;

    // 게시글 생성
    @PostMapping("/chatposts")
    public ChatPostResponseDto createChatpost(@RequestBody ChatPostRequestDto chatPostRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails)  {
        System.out.println("게시글 생성 되나요?");
        return chatPostService.createChatpost(chatPostRequestDto, userDetails);
    }
    // 게시글 수정
    @PutMapping("/chatposts/{chatpostId}")
    public ChatPostDetailDto updateChatPost(@PathVariable Long chatpostId,@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ChatPostRequestDto chatPostRequestDto) throws IOException {
        return chatPostService.updateChatPost(chatpostId, userDetails.getUser(), chatPostRequestDto);
    }
    // 게시글 삭제
    @DeleteMapping("/chatposts/{chatpostId}")
    public void deleteChatPost(@PathVariable Long chatpostId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        chatPostService.deleteChatPost(chatpostId, userDetails.getUser());
    }
    // 채팅게시글 참여하기 버튼
    @PostMapping("/chatposts/attend/{chatpostId}")
    public ChatPostAttendResponseDto attendChatPost(@PathVariable Long chatpostId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatPostService.attendChatPost(chatpostId, userDetails);
    }

    // 채팅 게시글 디테일 조회
    @GetMapping("/chatposts/detail/{chatpostId}")
    public ChatPostDetailDto findChatPost(@PathVariable Long chatpostId, UserDetailsImpl userDetails) {
        return chatPostService.findChatPost(chatpostId);
        }
    // 채팅 게시글 전제 조회(페이지)
    @GetMapping("/chatposts/{pageNum}")
    public ChatPostListDto getAllChatPost(@PathVariable int pageNum) {
        return new ChatPostListDto(chatPostService.getAllChatpost(pageNum-1));
    }

}
