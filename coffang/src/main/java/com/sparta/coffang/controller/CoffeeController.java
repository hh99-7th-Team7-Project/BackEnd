package com.sparta.coffang.controller;


import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.CoffeeService;
import com.sparta.coffang.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
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
    public ResponseEntity coffeeDel(@PathVariable String brand, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        return coffeeService.del(brand, id);
    }

    @GetMapping("/coffees")
    public ResponseEntity getAllCoffee(){
        return coffeeService.getAll();
    }

    @GetMapping("/coffees/random/{brand}/{category}/{min}/{max}")
    public ResponseEntity randCoffee(@PathVariable String brand, @PathVariable String category, @PathVariable Long min, @PathVariable Long max) {
        return coffeeService.getRandom(brand, category, min, max);
    }

    //커피 하나
    @GetMapping("/coffees/{brand}/{id}")
    public ResponseEntity getCoffee(@PathVariable String brand, @PathVariable Long id) {
        return coffeeService.getDetail(brand, id);
    }

    //커피 하나 로그인
    @GetMapping("/auths/coffees/{brand}/{id}")
    public ResponseEntity getCoffeeWithLogIn(@PathVariable String brand, @PathVariable Long id,
                                             @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeService.getDetailWithLogIn(brand, id, userDetails);
    }

    //브랜드 별 전체 커피
    @GetMapping("/coffees/{brand}")
    public ResponseEntity brandCoffees(@PathVariable String brand) {
        return coffeeService.getAllByBrand(brand);
    }

    //가격 순 정렬
    @GetMapping("/coffees/prices")
    public ResponseEntity getCoffeeByOrder(@RequestParam(required = false) String brand, @RequestParam(required = false) String category){
        List<String> validCategory = Arrays.asList("COFFEE", "NONCOFFEE", "TEA", "SMOOTHIE", "ADE");

        if (category != null && !validCategory.contains(category))
            throw new CustomException(ErrorCode.INVALID_CATEGORY);
        else if (brand == null && category == null)
            throw new CustomException(ErrorCode.INVALID_CATEGORY_AND_BRAND);

        return coffeeService.getByPriceOrder(brand, category);
    }

    //카테고리
    @GetMapping("/coffees/category")
    public ResponseEntity getCategory(@RequestParam(required = false) String keyword) {
        List<String> validCategory = Arrays.asList("COFFEE", "NONCOFFEE", "TEA", "SMOOTHIE", "ADE");

        if (keyword != null && !validCategory.contains(keyword))
            throw new CustomException(ErrorCode.INVALID_CATEGORY);

        return coffeeService.getByCategory(keyword);
    }

    //브랜드 + 카테고리
    @GetMapping("/coffees/{brand}/category")
    public ResponseEntity getCategoryAndBrand(@RequestParam(required = false) String keyword, @PathVariable String brand) {
        List<String> validCategory = Arrays.asList("COFFEE", "NONCOFFEE", "TEA", "SMOOTHIE", "ADE");

        if (keyword != null && !validCategory.contains(keyword))
            throw new CustomException(ErrorCode.INVALID_CATEGORY);

        return coffeeService.getByBrandAndCategory(keyword, brand);
    }

    /*
    검색
    null 값 허용하기 위해 쿼리스트링 사용
     */
    @GetMapping("/coffees/searches")
    public ResponseEntity searchCoffee(@RequestParam(required = false) String keyword){
        System.out.println(keyword);
        return coffeeService.search(keyword);
    }

    //커피 이미지만 1개 등록
    @PostMapping("/coffees/image")
    public ResponseEntity imageUpload(@RequestPart("imgUrl") List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<PhotoDto> photoDtos = s3Service.uploadFile(multipartFiles);
        return coffeeService.imageUpload(photoDtos.get(0));
    }

    //커피 이미지만 1개 조회
    @GetMapping("/coffees/image/{imageId}")
    public ResponseEntity getImage(@PathVariable Long imageId) {
        return coffeeService.getImage(imageId);
    }
}

