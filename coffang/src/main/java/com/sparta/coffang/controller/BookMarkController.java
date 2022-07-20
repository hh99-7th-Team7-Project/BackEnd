package com.sparta.coffang.controller;


import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.BookMarkService;
import com.sparta.coffang.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookMarkController {
    private final BookMarkService bookMarkService;


    @PostMapping("/coffees/bookmark/{brand}/{id}")
    public ResponseEntity Bookmark(@PathVariable String brand, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
         return bookMarkService.BookMark(userDetails.getUser(), brand, id);
    }

 }

