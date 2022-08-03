package com.sparta.coffang.email;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    //이메일 전송
    @PostMapping("/signup/emails")
    public ResponseEntity sendEmail (@RequestBody EmailRequestDto requestDto) {
        return emailService.sendEmail(requestDto);
    }

    //인증 코드 검사
    @PostMapping("/signup/emails/checks")
    public ResponseEntity checkEmail (@RequestBody EmailCodeRequestDto requestDto) {
        return emailService.checkEmailCode(requestDto);
    }
}
