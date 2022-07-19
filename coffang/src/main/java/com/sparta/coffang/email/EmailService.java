package com.sparta.coffang.email;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private String nickName = "Coffind"; // 메일내용에 쓰이는 거 같고
    private String username = "coffind"; // 네이버 아이디 인 거 같다
    @Value("${spring.mail.username}")
    private String email;
    @Value("${spring.mail.password}")
    private String password;

    private final EmailCodeRepository emailCodeRepository;

    //회원가입시 코드 전송
    public ResponseEntity sendEmail(EmailRequestDto requestDto) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", 465);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.naver.com");
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
//        session.setDebug(true); // 연결 잘 되는지 서버에서 확인 가능

        // 임의의 code 생성
        Random random = new Random();
        String code = String.valueOf(random.nextInt(888888) + 111111); // 범위 : 111111 ~ 999999

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email, nickName)); // 별명 설정
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(requestDto.getUsername()));
            mimeMessage.setSubject("[COFFIND] 이메일 인증번호 입니다.");
            mimeMessage.setText(new StringBuffer().append("커피를 사랑하는 COFFIND 입니다.")
                    .append("\n").append("회원가입을 진심으로 환영합니다.")
                    .append("\n").append("페이지로 돌아가서 인증코드를 적어주세요!!")
                    .append("\n")
                    .append("\n").append("이메일 인증번호: " + code)
                    .append("\n")
                    .append("\n").append("커피와 함께 더 즐거운 하루가 되세요.")
                    .toString());
            EmailCode emailCode1 = emailCodeRepository.findByUsername(requestDto.getUsername()).orElse(null);
            if (emailCode1 == null) {
                EmailCode emailCode = new EmailCode();
                emailCode.setUsername(requestDto.getUsername());
                emailCode.setCode(code);
                emailCodeRepository.save(emailCode);
            } else if (emailCode1.getUsername().equals(requestDto.getUsername())) {
                emailCode1.setCode(code);
                emailCodeRepository.save(emailCode1);
            }
            Transport.send(mimeMessage);
            return ResponseEntity.ok("이메일 전송 성공 ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("이메일 전송 실패");
        }
    }

    // 회원가입시 코드 확인
    public ResponseEntity checkEmailCode(EmailCodeRequestDto requestDto) {
        EmailCode emailCode = emailCodeRepository.findByCode(requestDto.getCode()).orElse(null);
        if (emailCode == null || !emailCode.getCode().equals(requestDto.getCode())) {
            return ResponseEntity.badRequest().body("인증코드가 일치하지 않습니다.");
        }
        emailCodeRepository.delete(emailCode);
        return ResponseEntity.ok("이메일 인증이 성공적으로 완료되었습니다.");
    }
}
