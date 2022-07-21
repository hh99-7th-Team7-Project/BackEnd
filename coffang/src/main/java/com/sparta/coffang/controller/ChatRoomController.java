package com.sparta.coffang.controller;

import com.sparta.coffang.dto.chatMessageDto.ChatMessagedResponseDto;
import com.sparta.coffang.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatService chatService;



    // 게시글 페이지 채널 채팅 내역 조회
    @GetMapping("/postchat/get/{chatpostId}")
    public List<ChatMessagedResponseDto>getPostMessage(@PathVariable Long chatpostId) {
        System.out.println("게시글페이지 채팅 내역 조회");
        List<ChatMessagedResponseDto> chatMessagedResponseDtoList = chatService.getPostMessage(chatpostId);
        System.out.println("겟포스트 완료!");
        return chatMessagedResponseDtoList;
    }


}
