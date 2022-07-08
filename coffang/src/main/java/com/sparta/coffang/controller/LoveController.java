package com.sparta.coffang.controller;


import com.sparta.coffang.model.Coffee;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.repository.CoffeeRespoistory;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import com.sparta.coffang.service.LoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoveController {
    private final LoveRepository loveRepository;
    private final CoffeeRespoistory coffeeRespoistory;
    private final LoveService loveService;

    @GetMapping("/loves/{brand}/{id}")
    public ResponseEntity love(@PathVariable String brand, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);

        if (loveRepository.existsByUserNicknameAndCoffeeId(userDetails.getUser().getNickname(), id)) {
            return ResponseEntity.ok().body("True");
        }
        return ResponseEntity.ok().body("오류");
    }

    @PostMapping("/coffee/{brand}/{id}")
    public void Love(@PathVariable String brand, @PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        loveService.Love(userDetails.getUser(), brand, id);
    }


//    @GetMapping("/loves/{brand}/{id}/{userid}")
//    public ResponseEntity loveudate(@PathVariable String brand, @PathVariable Long id) {
//        Coffee coffee = coffeeRespoistory.findByBrandAndId(brand, id);
//
//        if (loveRepository.existsByUserAndCoffee(coffee)) {
//            return ResponseEntity.ok().body("True");
//        }
//        loveRepository.save(new com.sparta.coffang.model.Love(userDetails.getUser(), coffee));
//        return ResponseEntity.ok().body("True");
//    }
}
