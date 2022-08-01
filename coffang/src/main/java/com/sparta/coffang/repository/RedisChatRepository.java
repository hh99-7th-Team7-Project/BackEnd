package com.sparta.coffang.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class RedisChatRepository {

    /* Redis CacheKeys */

    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO";

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;


    /* 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장 */
    public void setUserEnterInfo(String sessionId, String destination) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, destination);
    }

    /* 유저 세션으로 입장해 있는 채팅방 ID 조회 */
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    /* 유저 세션정보와 맵핑된 채팅방ID 삭제 */
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    /* ***채팅방 유저수 조회 */
    public long getUserCount(String destination) {
        return Long.valueOf(
                Optional.ofNullable(valueOps.get(USER_COUNT +"_"+ destination))
                        .orElse("0"));
    }
    /* 채팅방에 입장한 유저수 +1 */
    public long plusUserCount(String destination) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT +"_"+ destination))
                .orElse(0L);
    }

    /* 채팅방에 입장한 유저수 -1 */
    public long minusUserCount(String destination) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT +"_"+ destination))
                .filter(count -> count > 0)
                .orElse(0L);
    }
}
