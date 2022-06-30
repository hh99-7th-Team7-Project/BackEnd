package com.sparta.coffang.service;

import com.sparta.coffang.dto.LoveDto;
import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.model.Coffee;

import com.sparta.coffang.model.Love;

import com.sparta.coffang.model.Price;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.PriceRepository;
import com.sparta.coffang.repository.UserRepository;
import lombok.RequiredArgsConstructor;

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


    @Transactional
    public ResponseEntity save(String brand, CoffeeRequestDto coffeeRequestDto) {

        Coffee coffee = Coffee.builder()
                .img(coffeeRequestDto.getImg())
                .name(coffeeRequestDto.getName())
                .brand(brand)
                .category(coffeeRequestDto.getCategory())
                .build();
        coffeeRespoistory.save(coffee);

        List<Price> prices = savePrice(coffeeRequestDto, coffee);

        return ResponseEntity.ok().body(getResponseDto(coffee, prices));
    }


    public ResponseEntity edit(String brand, Long id, CoffeeRequestDto coffeeRequestDto) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);
        List<Price> prices = priceRepository.findAllByCoffeeIdAndCoffeeBrand(id, brand);

        //커피의 prices 다 삭제해버리고 추가
        //만약 커피의 기존 price가 3개인데 2개로 줄이고 싶으면 답이 없음
        for (Price price : prices) {
            priceRepository.delete(price);
        }
        coffee.setCoffee(coffeeRequestDto, brand);
        coffeeRespoistory.save(coffee);

        List<Price> newPrice = savePrice(coffeeRequestDto, coffee);

        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices()));
    }

    @Transactional
    public ResponseEntity del(String brand, Long id, CoffeeRequestDto requestDto) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        coffeeRespoistory.delete(coffee);
        return ResponseEntity.ok().body("삭제완료");
    }


    public ResponseEntity getAllByBrand(String brand) {
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();
        List<Coffee> coffees = coffeeRespoistory.findAllByBrand(brand);

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices()));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }

    public ResponseEntity getRandom() {
        List<Coffee> coffees = coffeeRespoistory.findAll();
        Random random = new Random();
        Coffee coffee = coffees.get(random.nextInt(coffees.size()));

        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices()));

    }

    public ResponseEntity getByBrandAndId(String brand, Long id) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        return ResponseEntity.ok().body(getResponseDto(coffee, coffee.getPrices()));
    }

    public ResponseEntity getByCategory(String category) {
        List<Coffee> coffees = coffeeRespoistory.findAllByCategory(category);
        List<CoffeeResponseDto> coffeeResponseDtos = new ArrayList<>();

        for (Coffee coffee : coffees) {
            coffeeResponseDtos.add(getResponseDto(coffee, coffee.getPrices()));
        }

        return ResponseEntity.ok().body(coffeeResponseDtos);
    }


    public CoffeeResponseDto getResponseDto(Coffee coffee, List<Price> prices) {
        List<Map<String, Object>> pricePair = new ArrayList<>();

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
                .build();

        return coffeeResponseDto;
    }

    @Transactional
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


    //추가본 추가본

    public Long loveCount(Long coffeeId, Long userId) {

        Coffee coffee = coffeeRespoistory.findById(coffeeId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        //loveCount 세는 조건문
        List<Love> loveList = loveRepository.findAllByCoffeeId(coffeeId);
        Long loveCount = 0L;
        for (Love love : loveList) {
            boolean is_check = love.getIs_check();
            if (is_check) {
                loveCount += 1;
            }
        }

        System.out.println("포스트에 loveCount 업데이트 수 (진행 전) : " + loveCount);
        coffee.LoveCount(loveCount);
        coffeeRespoistory.save(coffee);
        System.out.println("포스트에 loveCount 업데이트 수 : " + loveCount);

        return loveCount;
    }
    //is_check 업데이트
public void loveCheck(Long coffeeId, Long userId) {
        Coffee coffee = coffeeRespoistory.findById(coffeeId).orElseThrow(
                () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
        );

        List<Love> loveList = loveRepository.findAllByCoffeeId(coffeeId);
        for (Love love : loveList) {
            if (love.getUserId().equals(userId)) {
                love.Is_check(true);
                loveRepository.save(love);
            }
        }
    }


    public List<CoffeeResponseDto> myloveResponse(Long userId) {

        List<Love> loveList = loveRepository.findAllByUserId(userId);

        //빈 리스트 선언
        List<CoffeeResponseDto> coffeeResponseDtoList = new ArrayList<>();

        for (Love love : loveList) {
            Long coffeeId = love.getCoffeeId();

            Coffee coffee = coffeeRespoistory.findById(coffeeId).orElseThrow(
                    () -> new IllegalArgumentException("아이디가 존재하지 않습니다.")
            );

            Long loveCount = coffee.getLoveCount();
            System.out.println("내가 작성한 게시글 조회 서비스에 love count : " + loveCount);

            CoffeeResponseDto coffeeResponseDto = new CoffeeResponseDto(coffee, loveCount);

            coffeeResponseDtoList.add(coffeeResponseDto);

        }

        return coffeeResponseDtoList;
    }
    public CoffeeResponseDto Coffee(CoffeeResponseDto coffeeResponseDto) {

        Long coffeeId = coffeeResponseDto.getId();

        Coffee coffee = coffeeRespoistory.findById(coffeeId).orElseThrow(
                () -> new IllegalArgumentException("포스트가 존재하지 않습니다.")
        );
        Long coffeeUserId = coffee.getUserId();

        String nickname = userRepository.findById(coffeeId).get().getNickname();

        Long userId = coffeeResponseDto.getUserId();
        System.out.println("userId 0 체크 전");

        // is_Check
        if (userId==0) {
            System.out.println("게시글 상세 userId가 0일 때 : " +userId);
            boolean is_check = false;

            Long loveCount = coffee.getLoveCount();

            LoveDto love = new LoveDto(is_check,loveCount);

            CoffeeResponseDto coffeeResponseDto1 = new CoffeeResponseDto(coffee,love,nickname);
            return coffeeResponseDto1;

        } else {
            System.out.println("Optional<Likes> like 생성 전");

            if(loveRepository.findByUserIdAndCoffeeId(userId,coffeeId).isPresent()){
                System.out.println("Optional<Likes> like가 있음");
                Optional<Love> love = loveRepository.findByUserIdAndCoffeeId(userId,coffeeId);

                boolean is_check = love.get().getIs_check();

                Long loveCount = coffee.getLoveCount();

                System.out.println("게시글 상세조회 userId 0이 아닐 때 likeCount : " + loveCount );
                LoveDto loves = new LoveDto(is_check,loveCount);
                System.out.println("LikeDto 생성완료");
                CoffeeResponseDto detailPostResponseDto = new CoffeeResponseDto(coffee,loves,nickname);
                System.out.println("DetailPostResponseDto 생성완료");
                return detailPostResponseDto;

            } else{

                boolean is_check = false;
                System.out.println("게시글 상세조회 userId 0이 아닐 때 is_check : " + is_check );

                Long loveCount = coffee.getLoveCount();
                System.out.println("게시글 상세조회 userId 0이 아닐 때 likeCount : " + loveCount );
                LoveDto love = new LoveDto(is_check,loveCount);
                System.out.println("LoveDto 생성완료");
                CoffeeResponseDto coffeeResponseDto1 = new CoffeeResponseDto(coffee,love,nickname);
                System.out.println("CoffeeResponseDto 생성완료");
                return coffeeResponseDto1;
            }


        }

    }

    public List<CoffeeResponseDto> mypageResponse(Long userId) {

        List<Coffee> coffeeList = coffeeRespoistory.findAllByUserId(userId);

        //빈 리스트 선언
        List<CoffeeResponseDto> coffeeResponseDtoList = new ArrayList<>();

        //반복문 돌아서 DetailPostResponseDto 생성후 위에 리스트에 추가
        for (Coffee coffee : coffeeList) {

            Long loveCount = coffee.getLoveCount();
            System.out.println("내가 작성한 게시글 조회 서비스에 like count : "+loveCount);

            CoffeeResponseDto coffeeResponseDto = new CoffeeResponseDto(coffee,loveCount);

            coffeeResponseDtoList.add(coffeeResponseDto);

        }
        return coffeeResponseDtoList;
    }
}

