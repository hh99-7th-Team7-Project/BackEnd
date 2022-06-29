package com.sparta.coffang.repository;

import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {
    List<Price> findAllByCoffeeIdAndCoffeeBrand(Long id, String brand);
}
