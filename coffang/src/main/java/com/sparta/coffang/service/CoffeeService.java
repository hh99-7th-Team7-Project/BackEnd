package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.CoffeeRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Price;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.PriceRepository;
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

    public ResponseEntity getByPriceOrder(){
        List<Coffee> coffees = coffeeRespoistory.findAll();
        quickSort(coffees, 0, coffees.size() - 1);

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
    public List<Price> savePrice(CoffeeRequestDto coffeeRequestDto, Coffee coffee){
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
}
