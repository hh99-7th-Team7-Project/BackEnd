package com.sparta.coffang.repository;

import com.sparta.coffang.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPostId(long id);


}
