package com.sparta.coffang.service;

import com.sparta.coffang.dto.responseDto.PostLoveResponseDto;
import com.sparta.coffang.model.*;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.PostLoveRepository;
import com.sparta.coffang.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLoveService {
    private final PostLoveRepository postLoveRepository;
    private final PostRepository postRepository;

    //) -> {throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);}
//.orElseThrow(() -> new IllegalArgumentException(""))
    @Transactional
    public ResponseEntity PostLove(User user,Long postid) {
        Post post = postRepository.findAllById(postid);

        PostLove existPostLove = postLoveRepository.findByUserIdAndPostId(user.getId(), postid);

        if (postLoveRepository.existsByUserNicknameAndPostId(user.getNickname(), postid)) {
            postLoveRepository.deleteById(existPostLove.getLoveId());
            post.setLoveSize(-1L);
        }
        //love 이미 존재하지 않으면 생성
        else {
            PostLove postLove = new PostLove(user, post);

            postLoveRepository.save(postLove);
            System.out.println("PostLove 생성");
        }
        return ResponseEntity.ok().body(getLoveResponseDto(user,post));
    }
    public PostLoveResponseDto getLoveResponseDto(User user , Post post) {
        int loveCount = 0;

        //좋아요 체크 하는 부분
        if ((post.getLoveList() != null)) {
            loveCount = post.getLoveList().size()-1;
        }

        PostLoveResponseDto postLoveResponseDto = PostLoveResponseDto.builder()
                .postlove(postLoveRepository.existsByUserNicknameAndPostId(user.getNickname(), post.getId()))
                .postloveCount(loveCount)
                .build();
        return postLoveResponseDto;
    }
}


