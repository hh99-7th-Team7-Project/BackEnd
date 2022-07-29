package com.sparta.coffang.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column //신고당한 유저
    private Long userId;

    @Column //몇번 신고 당했는지
    private int reportNum;

    public BlackList(Long userId, int reportNum) {
        this.userId = userId;
        this.reportNum = reportNum;
    }
}
