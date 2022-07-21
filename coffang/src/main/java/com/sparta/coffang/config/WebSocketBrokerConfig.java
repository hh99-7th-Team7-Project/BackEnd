package com.sparta.coffang.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//메시지 브로커 설정하겠다
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private final FilterChannelInterceptor filterChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue","/topic"); //스프링 내장 브로커 사용하겠다, 파라미터 갑은 저 주소로 들어왔을때 메시지 처리하겠다
        registry.setApplicationDestinationPrefixes("/app"); //바로 브로커로 가는 경우가아니라 메시지가 가공이 필요할 때 핸들러로 보냄
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-coala")
                .setAllowedOriginPatterns("*")
                .withSockJS(); //SockJS 사용하기 위해 추가
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(filterChannelInterceptor);
    }


}
