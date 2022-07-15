package com.sparta.coffang.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column
    private String profileImage;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING) //DB갈 때 올 때 값을 String으로 변환해줘야함
    private UserRoleEnum role;

    @Column(unique = true)
    private String socialId;

    public User(String username, String nickname, String password, String profileImage, UserRoleEnum role) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.socialId =null;
    }

    //소셜 로그인
    public User(String username, String nickname, String password, String profileImage, UserRoleEnum role, String socialId) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.socialId = socialId;
    }

}
