package com.sparta.coffang.config;


import com.sparta.coffang.repository.ChatRoomRepository;
import com.sparta.coffang.repository.RedisChatRepository;
import com.sparta.coffang.security.jwt.JwtDecoder;
import com.sparta.coffang.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final ChatService chatService;
    private final RedisChatRepository redisChatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == headerAccessor.getCommand()) {
                // 유저가 Websocket으로 connect()를 한 뒤 호출됨
                log.info("유저 connect 중");
                log.info("full message: {}", message);

        }else if(StompCommand.SUBSCRIBE == headerAccessor.getCommand()){
//일단 주석처리//////////////////////////////////////////////////////////////////////////////////////////////
            String destination = chatService.getRoomId(
                    Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("error"));

            String sessionId = (String) message.getHeaders()
                    .get("simpSessionId");

            log.info("=== SUBSCRIBE sessionId : " + sessionId);
            log.info(("=== SUBSCRIBE destination : " + destination));


            redisChatRepository.setUserEnterInfo(sessionId, destination);


            /* 채팅방의 인원수를 +1한다. */
            redisChatRepository.plusUserCount(destination);


        }else if(StompCommand.DISCONNECT == headerAccessor.getCommand()){
//일단 주석처리////////////////////////////////////////////////////////////////////////////////////////////////////
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("=== DISCONNECT sessionId : " + sessionId);

            String destination = redisChatRepository.getUserEnterRoomId(sessionId);

            /* 채팅방의 인원수를 -1한다. */
            redisChatRepository.minusUserCount(destination);
            log.info("=== DISCONNECT sessionId : " + sessionId);
            log.info(("=== DISCONNECT destination : " + destination));
            /* 퇴장한 클라이언트의 roomId 맵핑 정보를 삭제한다. */
            redisChatRepository.removeUserEnterInfo(sessionId);


        }

//        log.info("auth:{}", headerAccessor.getNativeHeader("Authorization"));


////        log.info(headerAccessor.getHeader("nativeHeaders").getClass());
//        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
//            String jwtToken = headerAccessor.getFirstNativeHeader("Authorization").substring(7);
//
////            log.info("juwtToken : {}", jwtToken);
//
//            jwtDecoder.isValidToken(jwtToken);
//
//            log.info("msg: {}", "토큰인증완료?");
//            log.info("=================================================================================");
//        }


        //throw new MessagingException("no permission! ");
        return message;
    }
}
