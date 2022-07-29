package com.sparta.coffang.report;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    BlackList findByUserId(Long userId);
}
