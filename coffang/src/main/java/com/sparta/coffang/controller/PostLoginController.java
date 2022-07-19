package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.PostLoginService;
import com.sparta.coffang.service.PostService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PostLoginController {
    private final PostLoginService postLoginService;
    private final S3Service s3Service;



    @GetMapping("/postslogin")
    public ResponseEntity get(@RequestParam(required = false) String orders, @RequestParam(required = false) String category,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (category != null)
            return postLoginService.getAllByCategory(category, userDetails);
        //else if(orders == like)

        return postLoginService.getAll(userDetails);

    }

    @GetMapping("/postslogin/{id}")
    public ResponseEntity getDetail(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postLoginService.addView(id);
        return postLoginService.getDetail(id, userDetails);
    }

    //@RequestParam String type
    @GetMapping("/postslogin/searches")
    public ResponseEntity searchPost(@RequestParam String keyword, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postLoginService.search(keyword,userDetails);
    }
}
