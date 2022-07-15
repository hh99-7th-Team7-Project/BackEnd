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
        return coffeeService.save(brand, coffeeRequestDto, photoDtos, userDetails);
    }

    @PutMapping("/coffees/{brand}/{id}")
    public ResponseEntity coffeeEdit(@PathVariable String brand, @PathVariable Long id, @RequestPart("coffee") CoffeeRequestDto coffeeRequestDto,
                                     @RequestPart("imgUrl") List<MultipartFile> multipartFiles, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        List<PhotoDto> photoDtos  = s3Service.uploadFile(multipartFiles);
        return coffeeService.edit(brand, id, coffeeRequestDto, photoDtos, userDetails);
    }

    @DeleteMapping("/coffees/{brand}/{id}")
    public ResponseEntity coffeeDel(@PathVariable String brand, @PathVariable Long id,
                                    @RequestBody CoffeeRequestDto coffeeRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(userDetails.getUser().getRole() != UserRoleEnum.ADMIN)
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        return coffeeService.del(brand, id, coffeeRequestDto);
    }

    @GetMapping("/coffees")
    public ResponseEntity getAllCoffee(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return coffeeService.getAll(userDetails);
    }

    @GetMapping("/coffees/random")
    public ResponseEntity randCoffee(@RequestParam(required = false) String category, @RequestParam(required = false) String brand,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeService.getRandom(brand, category, userDetails);
    }

    //브랜드 별 전체 커피
    @GetMapping("/coffees/{brand}")
    public ResponseEntity brandCoffees(@PathVariable String brand,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeService.getAllByBrand(brand, userDetails);
    }

    //커피 하나
    @GetMapping("/coffees/{brand}/{id}")
    public ResponseEntity getCoffee(@PathVariable String brand, @PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return coffeeService.getByBrandAndId(brand, id, userDetails);
    }

    //가격 순 정렬
    @GetMapping("/coffees/orders")
    public ResponseEntity getCoffeebyOrder(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return coffeeService.getByPriceOrder(userDetails);
    }

    //검색
    @GetMapping("/coffees/searches")
    public ResponseEntity searchCoffee(@RequestParam(required = false) String keyword,@AuthenticationPrincipal UserDetailsImpl userDetails){
        System.out.println(keyword);
        return coffeeService.search(keyword, userDetails);
    }

    //사이드바

    @GetMapping("/coffees/sidebars")
    public ResponseEntity getSidebar(@RequestParam(required = false) String category,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (category.equals("coffee") || category.equals("tea") || category.equals("smoothie") || category.equals("ade") || category.equals("noncoffee"))
            return coffeeService.getByCategory(category, userDetails);

        throw new CustomException(ErrorCode.API_NOT_FOUND);
    }


    //커피 이미지만 1개 등록
    @PostMapping("/coffees/image")
    public ResponseEntity imageUpload(@RequestPart("imgUrl") List<MultipartFile> multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<PhotoDto> photoDtos = s3Service.uploadFile(multipartFiles);
        return coffeeService.imageUpload(photoDtos.get(0));
    }
    //커피 이미지만 1개 조회
    @GetMapping("/coffees/image/{imageId}")
    public ResponseEntity getImage(@PathVariable Long imageId) {
        return coffeeService.getImage(imageId);
    }
}

