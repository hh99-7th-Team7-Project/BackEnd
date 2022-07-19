package com.sparta.coffang.controller;


import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.PostLoveRepository;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CoffeeService;
import com.sparta.coffang.service.LoveService;
import com.sparta.coffang.service.PostLoveService;
import com.sparta.coffang.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostLoveController {
    private final PostLoveRepository postLoveRepository;
    private final PostRepository postRespoistory;
    private final PostLoveService postLoveService;
    private final PostService postService;



    @PostMapping("/postslogin/postlove/{category}/{id}")
    public void PostLove(@PathVariable String category, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postLoveService.PostLove(userDetails.getUser(), category, id);
    }

 }

