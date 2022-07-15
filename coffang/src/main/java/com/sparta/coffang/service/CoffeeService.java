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
import com.sparta.coffang.repository.PriceRepository;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.UserService;

import com.sparta.coffang.model.Image;
import com.sparta.coffang.model.Price;
import com.sparta.coffang.model.Review;
import com.sparta.coffang.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@RequiredArgsConstructor
public class CoffeeService {
    private final CoffeeRespoistory coffeeRespoistory;
    private final PriceRepository priceRepository;
    //추가 작성본
    private final LoveRepository loveRepository;

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;


    @Transactional
    public ResponseEntity save(String brand, CoffeeRequestDto coffeeRequestDto, List<PhotoDto> photoDtos, UserDetailsImpl userDetails) {

        Coffee coffee = Coffee.builder()
                .img(photoDtos.get(0).getPath())
                .name(coffeeRequestDto.getName())
                .brand(brand)
                .category(coffeeRequestDto.getCategory())
                .build();
        coffeeRespoistory.save(coffee);

        List<Price> prices = savePrice(coffeeRequestDto, coffee);

        return ResponseEntity.ok().body(getResponseDto(coffee, prices, userDetails));
    }



    public ResponseEntity edit(String brand, Long id, CoffeeRequestDto coffeeRequestDto, List<PhotoDto> photoDtos, UserDetailsImpl userDetails) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);
        List<Price> prices = priceRepository.findAllByCoffeeIdAndCoffeeBrand(id, brand);

        //커피의 prices 다 삭제해버리고 추가
        //만약 커피의 기존 price가 3개인데 2개로 줄이고 싶으면 답이 없음
        for (Price price : prices) {
            priceRepository.deleteById(price.getId());
        }

        coffee.setCoffee(coffeeRequestDto, brand, photoDtos);
        coffeeRespoistory.save(coffee);

        List<Price> newPrice = savePrice(coffeeRequestDto, coffee);

        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices(), userDetails));
    }

    @Transactional
    public ResponseEntity del(String brand, Long id, CoffeeRequestDto requestDto) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);
        coffeeRespoistory.delete(coffee);
        return ResponseEntity.ok().body("삭제완료");
    }

    public ResponseEntity getAll(UserDetailsImpl userDetails) {
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();
        List<Coffee> coffees = coffeeRespoistory.findAll();

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    public ResponseEntity getAllByBrand(String brand, UserDetailsImpl userDetails) {
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();
        List<Coffee> coffees = coffeeRespoistory.findAllByBrand(brand);

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
            System.out.println("조회");
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    public ResponseEntity getRandom(String brand, String category, UserDetailsImpl userDetails) {
        //coffee가 아무 것도 없으면 zero division이 발생할 것이므로, 에러 처리 해줘야 함
        List<Coffee> coffees = coffeeRespoistory.findAllByCategoryAndBrand(category, brand);
        Random random = new Random();

        if (coffees.size() == 0)
            throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);


        Coffee coffee = coffees.get(random.nextInt(coffees.size()));
        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices(), userDetails));
    }

    public ResponseEntity getByBrandAndId(String brand, Long id, UserDetailsImpl userDetails) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        if (coffee == null)
            throw new CustomException(ErrorCode.COFFEE_NOT_FOUND);

        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices(), userDetails));
    }

    public ResponseEntity getByCategory(String category, UserDetailsImpl userDetails) {
        List<Coffee> coffees = coffeeRespoistory.findAllByCategory(category);
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    //검색
    public ResponseEntity search(String keyword, UserDetailsImpl userDetails) {
        List<Coffee> coffees = coffeeRespoistory.findByNameContainingIgnoreCase(keyword);
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    //가격순 정렬
    public ResponseEntity getByPriceOrder(UserDetailsImpl userDetails) {
        List<Coffee> coffees = coffeeRespoistory.findAll();
        quickSort(coffees, 0, coffees.size() - 1);

        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();

        for (Coffee coffee : coffees) {

            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }


    public CoffeeResponseDto getResponseDto(Coffee coffee, List<Price> prices, UserDetailsImpl userDetails) {
//        Love love = loveRepository.findByUserIdAndCoffeeId(userDetails.getUser().getId(), coffee.getId());
//        Love love = loveRepository.findByUserIdAndCoffeeId(userDetails.getUser().getId(), coffee.getId());
        List<Map<String, Object>> pricePair = new ArrayList<>();
        int loveCount = 0;


        //좋아요 체크 하는 부분
        if ((coffee.getLoveList() != null && coffee.getLoveList().size() > 0)) {
            System.out.println("비교중");
            loveCount = coffee.getLoveList().size();
//            loveCheck = coffee.isLovecheck();
        }
//            if ((loveRepository.existsByUserNicknameAndCoffeeId(userDetails.getUser().getNickname(),coffee.getId()))&& love.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
//                System.out.println("체크확인");
//                loveCheck = true;
//            } else {
//                System.out.println("ㅇㅇ");
//                loveCheck = false;
//            }
//        }else {
//            System.out.println("없음");
//        }


        for (Price price : prices) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("size", price.getSize());
            map.put("price", price.getPrice());
            pricePair.add(map);
        }

//        boolean loveCheck = loveRepository.existsByUserNicknameAndCoffeeId(love.getUser().getNickname(),love.getCoffee().getId());

        CoffeeResponseDto coffeeResponseDto = CoffeeResponseDto.builder()
                .id(coffee.getId())
                .name(coffee.getName())
                .brand(coffee.getBrand())
                .pricePair(pricePair)
                .img(coffee.getImg())
                .category(coffee.getCategory())
                .love(loveCount)
                .build();

        return coffeeResponseDto;
    }
    //잠깐 추 가

    public List<Price> savePrice(CoffeeRequestDto coffeeRequestDto, Coffee coffee) {
        List<Price> prices = new ArrayList<>();

        for (int i = 0; i < coffeeRequestDto.getPrice().size(); i++) {
            Price price = new Price(coffeeRequestDto.getPrice().get(i),
                    coffeeRequestDto.getSize().get(i),
                    coffee);
            priceRepository.save(price);
            prices.add(price);
        }

        return prices;
    }

    public void quickSort(List<Coffee> coffees, int p, int r) {
        if (p < r) {
            int q = partition(coffees, p, r);
            quickSort(coffees, p, q - 1);
            quickSort(coffees, q + 1, r);
        }
    }

    public int partition(List<Coffee> coffees, int p, int r) {
        Long x = coffees.get(r).getPrices().get(0).getPrice();
        int i = p - 1;

        for (int j = p; j < r; j++) {
            if (coffees.get(j).getPrices().get(0).getPrice() <= x) {
                i++;
                Coffee coffeeChange = coffees.get(j);
                coffees.set(j, coffees.get(i));
                coffees.set(i, coffeeChange);
            }
        }
        Coffee coffeeChange = coffees.get(i + 1);
        coffees.set(i + 1, coffees.get(r));
        coffees.set(r, coffeeChange);

        return i + 1;
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
