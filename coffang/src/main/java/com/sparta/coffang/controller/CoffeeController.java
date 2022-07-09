package com.sparta.coffang.controller;


import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CoffeeService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@CrossOrigin
public class CoffeeController {
    private final CoffeeService coffeeService;
    private final S3Service s3Service;

    @PostMapping("/coffees/{brand}")
    public ResponseEntity coffeePost(@PathVariable String brand, @RequestPart("coffee") CoffeeRequestDto coffeeRequestDto,
                                     @RequestPart("imgUrl") List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        List<PhotoDto> photoDtos = s3Service.uploadFile(multipartFiles);
        return coffeeService.save(brand, coffeeRequestDto, photoDtos);
    }

    @PutMapping("/coffees/{brand}/{id}")
    public ResponseEntity coffeeEdit(@PathVariable String brand, @PathVariable Long id, @RequestPart("coffee") CoffeeRequestDto coffeeRequestDto,
                                     @RequestPart("imgUrl") List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        List<PhotoDto> photoDtos  = s3Service.uploadFile(multipartFiles);
        return coffeeService.edit(brand, id, coffeeRequestDto, photoDtos);
    }

    @DeleteMapping("/coffees/{brand}/{id}")
    public ResponseEntity coffeeDel(@PathVariable String brand, @PathVariable Long id,
                                    @RequestBody CoffeeRequestDto coffeeRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        return coffeeService.del(brand, id, coffeeRequestDto);
    }

    @GetMapping("/coffees")
    public ResponseEntity getAllCoffee(){
        return coffeeService.getAll();
    }

    @GetMapping("/coffees/random")
    public ResponseEntity randCoffee(@RequestParam(required = false) String category, @RequestParam(required = false) String brand) {
        return coffeeService.getRandom(brand, category);
    }

    //브랜드 별 전체 커피
    @GetMapping("/coffees/{brand}")
    public ResponseEntity brandCoffees(@PathVariable String brand) {
        return coffeeService.getAllByBrand(brand);
    }

    //커피 하나
    @GetMapping("/coffees/{brand}/{id}")
    public ResponseEntity getCoffee(@PathVariable String brand, @PathVariable Long id) {
        return coffeeService.getByBrandAndId(brand, id);
    }

    //가격 순 정렬
    @GetMapping("/coffees/orders")
    public ResponseEntity getCoffeebyOrder(){
        return coffeeService.getByPriceOrder();
    }

    //검색
    @GetMapping("/coffees/searches")
    public ResponseEntity searchCoffee(@RequestParam(required = false) String keyword){
        System.out.println(keyword);
        return coffeeService.search(keyword);
    }

    //사이드바
    @GetMapping("/coffees/sidebars")
    public ResponseEntity getSidebar(@RequestParam(required = false) String category) {
        if (category.equals("coffee") || category.equals("tea") || category.equals("smoothie") || category.equals("aid") || category.equals("nonCoffee"))
            return coffeeService.getByCategory(category);

        throw new CustomException(ErrorCode.API_NOT_FOUND);
    }
}

