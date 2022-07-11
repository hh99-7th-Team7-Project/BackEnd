package com.sparta.coffang.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Image {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long imageId;

    @Column(nullable = false)
    private String img;

}
