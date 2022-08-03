package com.sparta.coffang.service;

import com.sparta.coffang.model.BookMark;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.BookMarkRepository;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class BookMarkService {
    private final BookMarkRepository bookMarkRepository;
    private final PostRepository postRepository;


    @Transactional
    public ResponseEntity BookMark(User user,Long postid) {
        Post post = postRepository.findAllById(postid);

        BookMark existBookMark = bookMarkRepository.findByUserIdAndPostId(user.getId(), postid);

        if (bookMarkRepository.existsByUserNicknameAndPostId(user.getNickname(), postid)) {
            bookMarkRepository.deleteById(existBookMark.getBookMarkId());
            System.out.println("BookMark 삭제");
        }
        //love 이미 존재하지 않으면 생성
        else {
            BookMark bookMark = new BookMark(user, post);

            bookMarkRepository.save(bookMark);
            System.out.println("BookMark 생성");
        }
        return ResponseEntity.ok().body(bookMarkRepository.existsByUserNicknameAndPostId(user.getNickname(), postid));
    }
}


