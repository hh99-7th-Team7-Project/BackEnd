package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.CommentRequestDto;
import com.sparta.coffang.dto.requestDto.ReviewRequestDto;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //댓글수정

    @PutMapping("/posts/{id}/comments/{commentid}")

    public ResponseEntity updateComment(@PathVariable Long commentid, @RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return commentService.updateComment(commentid, commentRequestDto, userDetails);
    }
    //댓글삭제

    @DeleteMapping("/posts/{id}/comments/{commentid}")
    public ResponseEntity deleteComment(@PathVariable("commentid") Long commentId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        commentService.deleteComment(commentId, userDetails);

        return ResponseEntity.ok().body(commentId);
    }

    //댓글등록
    @PostMapping("/posts/{id}/comments")
    public ResponseEntity createComment(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long id) {
        return commentService.createComment(commentRequestDto, id, userDetails);
    }
    //댓글조회
    @GetMapping("/posts/{id}/comments")
    public ResponseEntity viewComment(@PathVariable Long id) {
        return commentService.findComments(id);}
}


