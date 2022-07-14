package com.sparta.coffang.repository;


import com.sparta.coffang.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByImageId(Long imageId);
}
