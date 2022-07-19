package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.dto.responseDto.ImageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Image;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.Price;
import com.sparta.coffang.repository.*;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CoffeeLoginService {
    private final CoffeeRespoistory coffeeRespoistory;
    private final PriceRepository priceRepository;
    //추가 작성본
    private final LoveRepository loveRepository;

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public ResponseEntity getAll(UserDetailsImpl userDetails) {
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();
        List<Coffee> coffees = coffeeRespoistory.findAll();

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices(), userDetails));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }
    //전체 받아오기
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
        Love love = loveRepository.findByUserIdAndCoffeeId(userDetails.getUser().getId(), coffee.getId());
        List<Map<String, Object>> pricePair = new ArrayList<>();
        int loveCount = 0;
        boolean loveCheck = false;


        //좋아요 체크 하는 부분
        if ((coffee.getLoveList() != null && coffee.getLoveList().size() > 0)) {
            System.out.println("비교중");
            loveCount = coffee.getLoveList().size();

            if ((loveRepository.existsByUserNicknameAndCoffeeId(userDetails.getUser().getNickname(), coffee.getId())) &&
                love.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
            System.out.println("체크확인");
            loveCheck = true;
            } else {
                System.out.println("ㅇㅇ");
                loveCheck = false;
            }
        }else{
            System.out.println("없음");
        }



        for (Price price : prices) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("size", price.getSize());
            map.put("price", price.getPrice());
            pricePair.add(map);
        }


        CoffeeResponseDto coffeeResponseDto = CoffeeResponseDto.builder()
                .id(coffee.getId())
                .name(coffee.getName())
                .brand(coffee.getBrand())
                .pricePair(pricePair)
                .img(coffee.getImg())
                .category(coffee.getCategory())
                .love(loveCount)
                .loveCheck(loveCheck)
                .build();

        return coffeeResponseDto;
    }


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
