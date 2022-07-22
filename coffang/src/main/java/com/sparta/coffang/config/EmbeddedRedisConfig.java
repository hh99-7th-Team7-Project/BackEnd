package com.sparta.coffang.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/*
    채팅 서버 실행 시 Embedded Redis 서버도 동시에 실행되도록 설정해야한다.
    local 환경에서만 실행되도록 @Profile 선언
 */
@Profile("127.0.0.1")
@Configuration
public class EmbeddedRedisConfig {
    @Value("6379")
    private int redisPort;
    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}