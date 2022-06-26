package com.sparta.coffang.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; //email 형식인 username

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

//    @Column(unique = true)
//    private Long kakaoId;


    public User(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
//        this.kakaoId = null;
    }

    //카카오톡
//    public User(String username, String nickname, String password, Long kakaoId) {
//        this.username = username;
//        this.nickname = nickname;
//        this.password = password;
//        this.kakaoId = kakaoId;
//    }

//    public User(SignupRequestDto requestDto) {
//        this.username = username;
//        this.nickname = nickname;
//        this.password = password;
//    }
}
