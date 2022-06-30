package com.sparta.coffang.repository;

import com.sparta.coffang.model.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoffeeRespoistory extends JpaRepository<Coffee, Long> {
    List<Coffee> findAllByBrand(String brand);
    Coffee findByBrandAndId(String Brand, Long id);
//    Optional<Coffee> findById(Long id);
    List<Coffee> findAllByCategory(String category);

    List<Coffee> findAllByUserId(Long userId);
}
