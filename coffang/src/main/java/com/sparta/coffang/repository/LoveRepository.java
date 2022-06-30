//package com.sparta.coffang.repository;
//
//import com.sparta.coffang.model.Love;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface LoveRepository extends JpaRepository<Love, Long> {
//    Optional<Love> findByUserIdAndCoffeeId(Long userId, Long coffeeId);
//
//    List<Love> findAllByCoffeeId(Long coffeeId);
//
//    List<Love> findAllByUserId(Long userId);
//
//    Optional<Love> findByCoffeeId(Long coffeeId);
//}
