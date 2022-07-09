package com.sparta.coffang.repository;

import com.sparta.coffang.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    List<Post> findAllByCategory(String category);

    List<Post> findByTitleContainsIgnoreCase(String keyword);
}
