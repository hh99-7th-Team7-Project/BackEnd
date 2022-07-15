package com.sparta.coffang.controller;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.PostService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final S3Service s3Service;

    @PostMapping("/posts")
    public ResponseEntity save(@RequestBody PostRequestDto postRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.savaPost(postRequestDto, userDetails);
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity edit(@RequestBody PostRequestDto postRequestDto, @PathVariable Long id,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.editPost(postRequestDto, id, userDetails);
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity del(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return postService.delPost(id, userDetails);
    }

    @GetMapping("/posts")
    public ResponseEntity get(@RequestParam(required = false) String orders, @RequestParam(required = false) String category) {
        if (category != null)
            return postService.getAllByCategory(category);
        //else if(orders == like)

        return postService.getAll();

    }

    @GetMapping("/posts/{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        postService.addView(id);
        return postService.getDetail(id);
    }

    //@RequestParam String type
    @GetMapping("/posts/searches")
    public ResponseEntity searchPost(@RequestParam String keyword){
        return postService.search(keyword);
    }
}
