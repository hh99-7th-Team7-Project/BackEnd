package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public ResponseEntity savaPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .user(userDetails.getUser())
                .build();
        postRepository.save(post);
        return ResponseEntity.ok().body(post);
    }

    public ResponseEntity editPost(PostRequestDto postRequestDto, Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (post.get().getUser() == userDetails.getUser())
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        post.get().setPost(postRequestDto);

        return ResponseEntity.ok().body(post);
    }

    public ResponseEntity delPost(Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (post.get().getUser() == userDetails.getUser())
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        postRepository.delete(post.get());
        return ResponseEntity.ok().body("삭제완료");
    }

    public ResponseEntity getAllByCategory(String category){
        List<Post> postList = postRepository.findAllByCategory(category);

        return ResponseEntity.ok().body(postList);
    }

    public ResponseEntity getAll(){
        //timestamp 만들어지면 orderby로 find 바꾸기
        List<Post> postList = postRepository.findAll();

        return ResponseEntity.ok().body(postList);
    }

    public ResponseEntity getDetail(Long id){
        Optional<Post> post = postRepository.findById(id);

        return ResponseEntity.ok().body(post.get());
    }

}
