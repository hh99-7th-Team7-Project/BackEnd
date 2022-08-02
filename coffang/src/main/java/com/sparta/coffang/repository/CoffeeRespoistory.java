package com.sparta.coffang.repository;

import com.sparta.coffang.model.Coffee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CoffeeRespoistory extends JpaRepository<Coffee, Long> {
    List<Coffee> findAllByBrand(String brand);
    Coffee findByBrandAndId(String Brand, Long id);
//    Optional<Coffee> findById(Long id);

    List<Coffee> findAllByBrandAndName(String brand, String name);

    List<Coffee> findAllByCategory(String category);

    List<Coffee> findAllByBrandAndCategory(String brand, String category);

    List<Coffee> findAllByCategoryAndBrandAndPriceGreaterThanEqualAndPriceLessThan(String category, String brand, Long minPrice, Long maxPrice);

    List<Coffee> findAllByBrandAndPriceGreaterThanEqualAndPriceLessThan(String category, Long minPrice, Long maxPrice);

    List<Coffee> findAllByCategoryAndPriceGreaterThanEqualAndPriceLessThan(String brand, Long minPrice, Long maxPrice);

    List<Coffee> findByNameContainingIgnoreCase(String keyword);
//    List<Coffee> findAllByUserId(Long userId);
}
