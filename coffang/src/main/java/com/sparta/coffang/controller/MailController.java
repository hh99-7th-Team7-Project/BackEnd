package com.sparta.coffang.controller;

import com.sparta.coffang.dto.requestDto.MailRequestDto;
import com.sparta.coffang.service.MailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @PostMapping("/mail")
    public ResponseEntity execMail(@RequestBody MailRequestDto mailRequestDto) {
        return mailService.send(mailRequestDto);
    }
}

