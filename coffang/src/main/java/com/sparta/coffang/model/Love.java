package com.sparta.coffang.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Love {
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Id
    Long LoveId;

    @ManyToOne
    @JoinColumn(name = "user_nickname")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coffee_id")
    private  Coffee coffee;


    public Love(User user, Coffee coffee) {
        this.user = user;
        this.coffee = coffee;
        this.coffee.getLoveList().add(this);
    }
}

/*
1. love를 누르면 이제 DB에 값이 올라간다.
2. love를 다시 눌러서 취소하면 DB에서 삭제한다.
3. Coffee의 Get 요청을 할 때,




 */
