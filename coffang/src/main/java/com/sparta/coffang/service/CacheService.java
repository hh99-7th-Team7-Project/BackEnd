package com.sparta.coffang.service;


import com.sparta.coffang.model.Fword;
import com.sparta.coffang.repository.FwordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final FwordRepository fwordRepository;

    private static final String FWORDS = "FWORDS";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;

    public void setFwords() {
        List<Fword> fwordList = fwordRepository.findAll();
        for (int i = 0; i < fwordList.size(); i++) {
            hashOpsEnterInfo.put(FWORDS, fwordList.get(i).getFWord(), fwordList.get(i).getFWord());
        }
    }
    public String getFwords(String key) {
        return hashOpsEnterInfo.get(FWORDS, key);
    }

    //리스트로 비속어 담기
//    @Cacheable(cacheNames = "fwordStore", key ="#key")
//    public List<Fword> getCacheData(final String key) {
//        log.info("캐시에 데이터 없을 경우 출력");
//        List<Fword> fwordList = fwordRepository.findAll();
//
//        return fwordList;
//    }

}
