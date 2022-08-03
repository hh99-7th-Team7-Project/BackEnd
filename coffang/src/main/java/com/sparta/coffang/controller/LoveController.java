package com.sparta.coffang.controller;


import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CoffeeService;
import com.sparta.coffang.service.LoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoveController {
    private final LoveRepository loveRepository;
    private final CoffeeRespoistory coffeeRespoistory;
    private final LoveService loveService;
    private final CoffeeService coffeeService;



    @PostMapping("/coffees/love/{brand}/{id}")
    public ResponseEntity Love(@PathVariable String brand, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return loveService.Love(userDetails.getUser(), brand, id);
    }

 }

