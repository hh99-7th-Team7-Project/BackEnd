package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.CommentRequestDto;
import com.sparta.coffang.dto.responseDto.CommentResponseDto;
import com.sparta.coffang.dto.responseDto.ReviewResponseDto;
import com.sparta.coffang.model.Comment;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.repository.CommentRepository;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;







    //리뷰 생성

    public ResponseEntity createComment(CommentRequestDto commentRequestDto, Long id, UserDetailsImpl userDetails ) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 커피입니다."));

        System.out.println("post 검색");
        Comment comment= new Comment(
                commentRequestDto.getComment(),
                post,
                userDetails.getUser());


        System.out.println("comment 생성");
        commentRepository.save(comment);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .nickname(comment.getUser().getNickname())
                .build();

        System.out.println("코멘트 성공");
        return ResponseEntity.ok().body(commentResponseDto);
    }

    //댓글 조회
    public ResponseEntity findComments(Long id) {
        List<Comment> comments = commentRepository.findAllByPostId(id);
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                    .id(comment.getId())
                    .comment(comment.getComment())
                    .nickname(comment.getUser().getNickname())
                    .build();

            commentResponseDtos.add(commentResponseDto);

        }
        System.out.println("코멘트 검색 성공");
        return ResponseEntity.ok().body(commentResponseDtos);
    }
    //삭제
    public ResponseEntity deleteComment(Long commentId, UserDetailsImpl userDetails) {
        //삭제할 댓글이 있는지 확인
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));
        //삭제할 댓글의 유저와 현재 로그인한 유저가 같은지 확인
        if(!comment.getUser().getNickname().equals(userDetails.getUser().getNickname())){
            throw new IllegalArgumentException("자신의 리뷰만 삭제할 수 있습니다.");
        }

        System.out.println("delete commentId : " + commentId);
        commentRepository.deleteById(commentId);
        System.out.println("코멘트 삭제 성공");
        return ResponseEntity.noContent().build();
    }

    //댓글 수정
    @Transactional
    public ResponseEntity updateComment(Long commentId, CommentRequestDto commentRequestDto, UserDetailsImpl userDetails) {
        Comment comment =commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!comment.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
            comment.update(commentRequestDto);
            throw new IllegalArgumentException("자신의 댓글만 수정할 수 있습니다.");
        }

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(comment.getId())
                .comment(comment.getComment())
                .nickname(comment.getUser().getNickname())
                .build();

        comment.update(commentRequestDto);
        return ResponseEntity.ok().body(commentResponseDto);
    }
 }

