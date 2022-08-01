package com.sparta.coffang.service;

import com.sparta.coffang.dto.chatMessageDto.ChatMessageDto;
import com.sparta.coffang.dto.chatMessageDto.ChatMessagedResponseDto;
import com.sparta.coffang.model.ChatMessage;
import com.sparta.coffang.model.ChatRoom;
import com.sparta.coffang.repository.ChatMessageRepository;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }


    // 게시글 페이지 채널 채팅 내역 조회
    public List<ChatMessagedResponseDto> getPostMessage(Long chatpostId) {

        List<ChatMessage> chatMessageList = chatMessageRepository.findTOP20ByChatRoom_ChatpostIdOrderByCreatedAtDesc(chatpostId);

        List<ChatMessagedResponseDto> chatMessagedResponseDtoList = new ArrayList<>();
        for(ChatMessage chatMessage : chatMessageList) {
            ChatMessagedResponseDto chatMessagedResponseDto = new ChatMessagedResponseDto(chatMessage);

            chatMessagedResponseDtoList.add(chatMessagedResponseDto);
        }
        Collections.reverse(chatMessagedResponseDtoList);
        return chatMessagedResponseDtoList;
    }

//    /
    ///////////////////////////채팅 메시지 셋업 메소드////////////////////////////////////////////////////////////////////
    public void chatSettingMethod(ChatMessageDto chatMessageDto, String token, ChatRoom chatRoom) {
        chatMessageDto.setCreatedAt(LocalDateTime.now());
        Long id = 0L;
        String username = "";

        /* 토큰 정보 추출 */
        if (!(String.valueOf(token).equals("Authorization")||String.valueOf(token).equals("null"))) {
            log.info("token : {}",String.valueOf(token));
            String tokenInfo = token.substring(7);
            username = jwtDecoder.decodeUsername(tokenInfo);
//            uid = userRepository.findByUsername(username).get().getUid();
        }

        if(chatMessageDto.getStatus().equals("JOIN")) {
            if(username!=""&&username!="null"){
                log.info("JOIN일때 {}",chatMessageDto.getSenderName());
                chatMessageDto.setMessage(chatMessageDto.getSenderName()+"님이 입장하셨습니다");
                log.info("=== 연결 : {}",chatMessageDto.getChatpostId());
            }

        } else if (chatMessageDto.getStatus().equals("OUT")) {
            if(username!=""&&username!="null"){
                log.info("OUT일때 {}",chatMessageDto.getSenderName());
                chatMessageDto.setMessage( chatMessageDto.getSenderName()+"님이 퇴장하셨습니다");
                log.info("=== 연결끊김 : {}",chatMessageDto.getChatpostId());
            }

        } else {
            id = chatMessageDto.getId();
            String profileImage = userRepository.findById(id).get().getProfileImage();

            chatMessageDto.setProfileImage(profileImage);

            //채팅 메시지 저장
            ChatMessage chatMessage = new ChatMessage(id, chatMessageDto, chatRoom);
            chatMessageRepository.save(chatMessage);


        }

    }


}
