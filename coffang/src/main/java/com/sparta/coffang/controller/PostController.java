package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.PostService;
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
    public ResponseEntity get(@RequestParam(required = false) String keyword, @RequestParam(required = false) String category) {
        if (keyword == "category")
            return postService.getAllByCategory(category);

        return postService.getAll();

    }

    @GetMapping("/posts/{id}")
    public ResponseEntity getDetail(@PathVariable Long id){
        return postService.getDetail(id);
    }
}
