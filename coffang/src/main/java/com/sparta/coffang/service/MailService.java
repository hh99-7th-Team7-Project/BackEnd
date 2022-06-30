package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.MailRequestDto;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    public ResponseEntity send(MailRequestDto mailRequestDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        System.out.println(mailRequestDto.getAddress());
        System.out.println(mailRequestDto.getSender());

        message.setTo(mailRequestDto.getAddress());
        message.setFrom(mailRequestDto.getSender());
        message.setSubject(mailRequestDto.getTitle());
        message.setText(mailRequestDto.getContent());
        javaMailSender.send(message);

        return ResponseEntity.ok().body("메일 전송 성공");
    }
}
