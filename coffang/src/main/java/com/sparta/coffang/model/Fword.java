package com.sparta.coffang.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Fword {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String fWord;

    public Fword(String fWord) {
        this.fWord = fWord;
    }

    public void update (String fWord){
        this.fWord = fWord;
    }

}
