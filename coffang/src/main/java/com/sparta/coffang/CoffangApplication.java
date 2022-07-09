package com.sparta.coffang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CoffangApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffangApplication.class, args);
    }

}
