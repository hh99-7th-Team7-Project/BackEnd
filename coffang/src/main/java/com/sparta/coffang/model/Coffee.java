package com.sparta.coffang.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Coffee {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
}
