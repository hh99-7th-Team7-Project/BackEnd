package com.sparta.coffang.service;

import com.sparta.coffang.model.*;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.PostLoveRepository;
import com.sparta.coffang.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostLoveService {
    private final PostLoveRepository postLoveRepository;
    private final PostRepository postRepository;

    //) -> {throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);}
//.orElseThrow(() -> new IllegalArgumentException(""))
    @Transactional
    public void PostLove(User user, String category, Long postid) {
        Post post = postRepository.findByCategoryAndId(category, postid);

        PostLove existPostLove = postLoveRepository.findByUserIdAndPostId(user.getId(), postid);

        if (postLoveRepository.existsByUserNicknameAndPostId(user.getNickname(), postid)) {
            postLoveRepository.deleteById(existPostLove.getLoveId());
        }
        //love 이미 존재하지 않으면 생성
        else {
            PostLove postLove = new PostLove(user, post);

            postLoveRepository.save(postLove);
            System.out.println(postLove.getLoveId());
            System.out.println(post.getLoveList().size());
            System.out.println("PostLove 생성");
        }
    }
}


