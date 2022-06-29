package com.sparta.coffang.controller;


import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.service.CoffeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@CrossOrigin
public class CoffeeController {
    private final CoffeeService coffeeService;

    @PostMapping("/coffee/{brand}")
    public ResponseEntity coffeePost(@PathVariable String brand, @RequestBody CoffeeRequestDto coffeeRequestDto){
        return coffeeService.save(brand, coffeeRequestDto);
    }

    @PutMapping("/coffee/{brand}/{id}")
    public ResponseEntity coffeeEdit(@PathVariable String brand,
                                     @PathVariable Long id, @RequestBody CoffeeRequestDto coffeeRequestDto){
        return coffeeService.edit(brand, id, coffeeRequestDto);
    }

    @DeleteMapping("/coffee/{brand}/{id}")
    public ResponseEntity coffeeDel(@PathVariable String brand,
                                     @PathVariable Long id, @RequestBody CoffeeRequestDto coffeeRequestDto){
        return coffeeService.del(brand, id, coffeeRequestDto);
    }


    @GetMapping("/randcoffees")
    public ResponseEntity randCoffee(){
        return coffeeService.getRandom();
    }

    //브랜드 별 전체 커피
    @GetMapping("/coffee/{brand}")
    public ResponseEntity brandCoffees(@PathVariable String brand){
        return coffeeService.getAllByBrand(brand);
    }

    //커피 하나
    @GetMapping("/coffee/{brand}/{id}")
    public ResponseEntity getCoffee(@PathVariable String brand, @PathVariable Long id){
        return coffeeService.getByBrandAndId(brand, id);
    }

    //사이드바
    @GetMapping("/coffee")
    public ResponseEntity getSidebar(@RequestParam(required = false) String category){
        if(category.equals("coffee") || category.equals("nonCoffee"))
            return coffeeService.getByCategory(category);
        else{
            throw new CustomException(ErrorCode.API_NOT_FOUND);
        }
    }
}
