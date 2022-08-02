package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.dto.responseDto.ImageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.*;

import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.ImageRepository;

import com.sparta.coffang.model.Image;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
public class CoffeeService {
    private final CoffeeRespoistory coffeeRespoistory;
    private final LoveRepository loveRepository;
    private final ImageRepository imageRepository;

    @Transactional
    public ResponseEntity save(String brand, CoffeeRequestDto coffeeRequestDto, List<PhotoDto> photoDtos) {
        List<Coffee> coffees = new ArrayList<>();

        for (int i = 0; i < coffeeRequestDto.getPrice().size(); i++) {
            Coffee coffee = Coffee.builder()
                    .img(photoDtos.get(0).getPath())
                    .name(coffeeRequestDto.getName())
                    .brand(brand)
                    .category(coffeeRequestDto.getCategory())
                    .size(coffeeRequestDto.getSize().get(i))
                    .price(coffeeRequestDto.getPrice().get(i))
                    .build();
            coffeeRespoistory.save(coffee);
            coffees.add(coffee);
        }

        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    @Transactional
    public ResponseEntity edit(String brand, Long id, CoffeeRequestDto coffeeRequestDto, List<PhotoDto> photoDtos) {
        List<Coffee> coffeeList = coffeeRespoistory.findAllByBrandAndName(brand, coffeeRequestDto.getName());

        for (Coffee coffee : coffeeList) {
            coffeeRespoistory.delete(coffee);
        }
        coffeeRespoistory.flush();

        for (int i = 0; i < coffeeRequestDto.getPrice().size(); i++) {
            Coffee coffee = Coffee.builder()
                    .img(photoDtos.get(0).getPath())
                    .name(coffeeRequestDto.getName())
                    .brand(brand)
                    .category(coffeeRequestDto.getCategory())
                    .size(coffeeRequestDto.getSize().get(i))
                    .price(coffeeRequestDto.getPrice().get(i))
                    .build();
            coffeeRespoistory.save(coffee);
        }

        return ResponseEntity.ok().body("");
    }

    @Transactional
    public ResponseEntity del(String brand, Long id) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);
        coffeeRespoistory.delete(coffee);
        return ResponseEntity.ok().body("삭제완료");
    }

    public ResponseEntity getAll() {
        List<Coffee> coffees = coffeeRespoistory.findAll();

        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    public ResponseEntity getAllByBrand(String brand) {
        List<Coffee> coffees = coffeeRespoistory.findAllByBrand(brand);
        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    //랜덤커피
    public ResponseEntity getRandom(String brand, String category, Long min, Long max) {
        //coffee가 아무 것도 없으면 zero division이 발생할 것이므로, 에러 처리 해줘야 함
        Random random = new Random();
        List<Coffee> coffees = coffeeRespoistory.findAllByCategoryAndBrandAndPriceGreaterThanEqualAndPriceLessThan(category, brand, min, max);

        CoffeeResponseDto coffeeResponseDto = new CoffeeResponseDto();
        do {
            if (coffees.size() == 0)
                throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);

            int randNum = random.nextInt(coffees.size());
            Coffee coffee = coffees.get(randNum);
            List<Coffee> findCoffee = coffeeRespoistory.findAllByBrandAndName(coffee.getBrand(), coffee.getName());
            coffeeResponseDto = getResponseDto(findCoffee).get(0);
            coffees.remove(randNum);
        } while (Long.valueOf((String) coffeeResponseDto.getPricePair().get(0).get("price").toString().replaceAll(",", "")) < min
                || (Long.valueOf((String) coffeeResponseDto.getPricePair().get(0).get("price").toString().replaceAll(",", "")) > max));

        return ResponseEntity.ok().body(coffeeResponseDto);
    }

    //detail
    public ResponseEntity getDetail(String brand, Long id) {
        //굳이 2번 해야하나? 그냥 id가 아니라 coffee name으로 pathVariable 해서 받아오면 안 되나?
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        if (coffee == null)
            throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);

        List<Coffee> coffees = coffeeRespoistory.findAllByBrandAndName(brand, coffee.getName());
        return ResponseEntity.ok().body(getResponseDto(coffees));
    }
    public ResponseEntity getDetailWithLogIn(String brand, Long id, UserDetailsImpl userDetails) {
        //굳이 2번 해야하나? 그냥 id가 아니라 coffee name으로 pathVariable 해서 받아오면 안 되나?
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        if (coffee == null)
            throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);

        List<Coffee> coffees = coffeeRespoistory.findAllByBrandAndName(brand, coffee.getName());
        CoffeeResponseDto coffeeResponseDto = new CoffeeResponseDto(coffee);

        for (Coffee coffee1 : coffees) {
            coffeeResponseDto.setPricePair(coffee1);
        }

        coffeeResponseDto.setLoveCheck(loveRepository.existsByUserNicknameAndCoffeeId(userDetails.getUser().getNickname(), id));

        return ResponseEntity.ok().body(coffeeResponseDto);
    }

    public ResponseEntity getByCategory(String category) {
        List<Coffee> coffees = coffeeRespoistory.findAllByCategory(category);
        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    public ResponseEntity getByBrandAndCategory(String category, String brand) {
        List<Coffee> coffees = coffeeRespoistory.findAllByBrandAndCategory(brand, category);
        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    //검색
    public ResponseEntity search(String keyword) {
        List<Coffee> coffees = coffeeRespoistory.findByNameContainingIgnoreCase(keyword);

        return ResponseEntity.ok().body(getResponseDto(coffees));
    }

    //가격순 정렬
    public ResponseEntity getByPriceOrder(String brand, String category) {
        List<Coffee> coffees;

        if (brand == null)
            coffees = coffeeRespoistory.findAllByCategory(category);
        else if (category == null)
            coffees = coffeeRespoistory.findAllByBrand(brand);
        else
            coffees = coffeeRespoistory.findAllByBrandAndCategory(brand, category);

        List<CoffeeResponseDto> coffeeResponseDtos = getResponseDto(coffees);
        Collections.sort(coffeeResponseDtos, (a, b) -> (int) ((Long) a.getPricePair().get(0).get("price") + (Long) b.getPricePair().get(0).get("price")));

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    public List<CoffeeResponseDto> getResponseDto(List<Coffee> coffees) {
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();

        for (Coffee coffee : coffees) {
            CoffeeResponseDto coffeeDto = coffeeResponseDtos.stream()
                    .filter(coffeeResponseDto -> coffee.getName().equals(coffeeResponseDto.getName()))
                    .filter(coffeeResponseDto -> coffee.getBrand().equals(coffeeResponseDto.getBrand()))
                    .findAny()
                    .orElse(new CoffeeResponseDto(coffee));
            coffeeDto.setPricePair(coffee);

            if (coffeeDto.getPricePair().size() < 2)
                coffeeResponseDtos.add(coffeeDto);
        }

        return coffeeResponseDtos;
    }

    //커피 이미지만 1개 등록하기
    public ResponseEntity imageUpload(PhotoDto photoDto) {
        Image image = Image.builder()
                .img(photoDto.getPath())
                .build();
        imageRepository.save(image);

        ImageResponseDto imageResponseDto = ImageResponseDto.builder()
                .imageId(image.getImageId())
                .img(image.getImg())
                .build();

        return ResponseEntity.ok().body(imageResponseDto);
    }

    //커피 이미지만 1개 프론트로 내려주기
    public ResponseEntity getImage(Long imageId) {
        Image image = imageRepository.findByImageId(imageId);

        ImageResponseDto imageResponseDto = ImageResponseDto.builder()
                .imageId(image.getImageId())
                .img(image.getImg())
                .build();

        return ResponseEntity.ok().body(imageResponseDto);
    }
}
