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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
        return postService.savePost(postRequestDto, userDetails);
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
    public ResponseEntity get(@RequestParam(required = false) String category, Pageable pageable) {
        if (category.equals("love"))
            return postService.getAllOrderByLove(pageable);
        else if (category != null)
            return postService.getAllByCategory(category, pageable);


        return postService.getAll(pageable);
    }

    @GetMapping("/posts/auths")
    public ResponseEntity getWithLogIn(@RequestParam(required = false) String category, @AuthenticationPrincipal UserDetailsImpl userDetails, Pageable pageable) {
        if (category.equals("love"))
            return postService.getAllOrderByLove(pageable);
        else if (category != null)
            return postService.getAllByCategory(category, pageable);

        return postService.getAllWithLogIn(userDetails, pageable);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity getDetail(@PathVariable Long id) {
        postService.addView(id);
        return postService.getDetail(id);
    }

    @GetMapping("/posts/auths/{id}")
    public ResponseEntity getDetailWithLogin(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        postService.addView(id);
        return postService.getDetailWithLogIn(id, userDetails);
    }

    //@RequestParam String type
    @GetMapping("/posts/searches")
    public ResponseEntity searchPost(@RequestParam String keyword, Pageable pageable){
        return postService.search(keyword, pageable);
    }

    @GetMapping("/posts/auths/searches")
    public ResponseEntity searchPostWithLogin(@RequestParam String keyword, @AuthenticationPrincipal UserDetailsImpl userDetails, Pageable pageable){
        return postService.searchWithLogIn(keyword, userDetails, pageable);
    }
}
