package com.sparta.coffang.controller;


import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CoffeeLoginService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@CrossOrigin
public class CoffeeLoginController {
    private final CoffeeLoginService coffeeloginService;
    private final S3Service s3Service;


    @GetMapping("/coffeeslogin")
    public ResponseEntity getAllCoffee(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return coffeeloginService.getAll(userDetails);
    }

    @GetMapping("/coffeeslogin/random")
    public ResponseEntity randCoffee(@RequestParam(required = false) String category, @RequestParam(required = false) String brand,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeloginService.getRandom(brand, category, userDetails);
    }

    //브랜드 별 전체 커피
    @GetMapping("/coffeeslogin/{brand}")
    public ResponseEntity brandCoffees(@PathVariable String brand,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeloginService.getAllByBrand(brand, userDetails);
    }

    //커피 하나
    @GetMapping("/coffeeslogin/{brand}/{id}")
    public ResponseEntity getCoffee(@PathVariable String brand, @PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeloginService.getByBrandAndId(brand, id, userDetails);
    }

    //가격 순 정렬
    @GetMapping("/coffeeslogin/orders")
    public ResponseEntity getCoffeebyOrder(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return coffeeloginService.getByPriceOrder(userDetails);
    }

    //검색
    @GetMapping("/coffeeslogin/searches")
    public ResponseEntity searchCoffee(@RequestParam(required = false) String keyword,@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println(keyword);
        return coffeeloginService.search(keyword, userDetails);
    }

    //사이드바

    @GetMapping("/coffeeslogin/sidebars")
    public ResponseEntity getSidebar(@RequestParam(required = false) String category,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (category.equals("coffee") || category.equals("tea") || category.equals("smoothie") || category.equals("ade") || category.equals("noncoffee"))
            return coffeeloginService.getByCategory(category, userDetails);

        throw new CustomException(ErrorCode.API_NOT_FOUND);
    }

}

