package com.sparta.coffang.repository;

import com.sparta.coffang.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    List<Review> findAllByCoffeeIdOrderByCreatedAtDesc(long id);


}
