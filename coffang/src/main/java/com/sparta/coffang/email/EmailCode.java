package com.sparta.coffang.email;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class EmailCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String code;
}
