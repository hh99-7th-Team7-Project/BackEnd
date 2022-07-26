package com.sparta.coffang.repository;

import com.sparta.coffang.model.Attend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendRepository extends JpaRepository<Attend, Long> {


    List<Attend> findByChatpostId(Long chatpostId);

    Attend findByChatpostIdAndUserId(Long chatpostId, Long id);

    void deleteByChatpostIdAndUserId(Long chatpostId, Long id);

    //내가 참가한 모임수, 모임방 (마이페이지)
    List<Attend> findAllByUserId(Long userId);
    List<Attend> findAllByUserIdOrderByAttendIdDesc(Long userId);
}
