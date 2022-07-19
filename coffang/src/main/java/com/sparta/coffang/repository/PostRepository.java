package com.sparta.coffang.repository;

import com.sparta.coffang.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    List<Post> findAllByOrderByCreatedAtDesc();

    List<Post> findAllByCategory(String category);

    List<Post> findByTitleContainingIgnoreCase(String keyword);

    Post findByCategoryAndId(String category,Long id);


    List<Post> findByUserNicknameContainingIgnoreCase(String keyword);



    @Modifying
    @Query("update Post p set p.view = p.view + 1 where p.id = :id")
    int updateView(Long id);

    //내가 쓴 게시글 찾기
    List<Post> findAllByUserId(Long userId);

}
