
package com.sparta.coffang.controller;


import com.sparta.coffang.dto.chatMessageDto.ChatMessageDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.ChatRoom;
import com.sparta.coffang.repository.ChatMessageRepository;
import com.sparta.coffang.repository.ChatRoomRepository;
import com.sparta.coffang.repository.RedisChatRepository;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.jwt.JwtDecoder;
import com.sparta.coffang.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;


@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtDecoder jwtDecoder;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final RedisChatRepository redisChatRepository;
    private final ChatService chatService;



    //////////////////////////////////게시글 페이지 채널///////////////////////////////////////////////////////////////
    @Transactional
    @MessageMapping("/postchat")
    public void postMessage(ChatMessageDto chatMessageDto, @Header("Authorization") String token) throws Exception {
        System.out.println("게시글 페이지 채널");

        Thread.sleep(100); // simulated delay

        ChatRoom chatRoom = new ChatRoom();
        if (chatMessageDto.getTotalcount() <= chatMessageDto.getCount()) {
            System.out.println("왜 안나와요");
        } else {
            throw new NullPointerException("모집완료 되었습니다");
        }
        // 채팅방있는지 확인 후 없으면 생성, 있으면 채팅방 변수에 할당해놓음 -> 채팅 저장 시에 사용할 예정
        if(!(chatRoomRepository.findByChatpostId(chatMessageDto.getChatpostId()).isPresent())) {
            chatRoom = new ChatRoom("post",chatMessageDto.getChatpostId());
            chatRoomRepository.save(chatRoom);
            System.out.println("챗 룸"+chatRoom);
        } else {
            chatRoom = chatRoomRepository.findByChatpostId(chatMessageDto.getChatpostId()).orElseThrow(
                    ()-> new CustomException(ErrorCode.NOT_FOUND_CHATROOM)
            );
            System.out.println("챗 룸"+chatRoom);
        }
        System.out.println("채팅 메시지 셋업 메소드");
        //채팅 메시지 셋업 메소드
        chatService.chatSettingMethod(chatMessageDto, token, chatRoom);
        System.out.println("채팅 메시지 셋업 완료");
        String destination = String.valueOf(chatMessageDto.getChatpostId());
        log.info("=== channel : {}",destination);
        System.out.println("destination"+destination);
        chatMessageDto.setUserCount(redisChatRepository.getUserCount(destination));

        simpMessagingTemplate.convertAndSend("/topic/postchat"+"/"+destination ,chatMessageDto);
        System.out.println("게시글 채널 완료!");
    }


}
