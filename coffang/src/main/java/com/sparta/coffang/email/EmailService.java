package com.sparta.coffang.email;

import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmailService {
    private String nickName = "Unanimous";;
    @Value("${spring.mail.username}")
    private String email;
    private String username = "team-unanimous";
    @Value("${spring.mail.password}")
    private String password;

    private final UserRepository userRepository;

    //회원가입시 코드 전송
    public ResponseEntity emailSend(SignupRequestDto requestDto) {
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
        session.setDebug(true);
        String username = requestDto.getUsername();
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"; //이메일 정규식 패턴
        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals(""))
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        else if (!Pattern.matches(emailPattern, username))
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        else if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        String code = "";
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
            code = key.toString();
        }
        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(email, nickName));        // 별명 설정
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(requestDto.getUsername()));
            mimeMessage.setSubject("제목");
            mimeMessage.setText(new StringBuffer().append("안녕하세요! Unanimous 입니다.")
                    .append("\n").append("홈페이지로 돌아가서 이메일 인증코드를 입력해주세요.")
                    .append("\n")
                    .append("\n").append("이메일 인증코드: " + code)
                    .append("\n")
                    .append("\n").append("서비스를 이용해 주셔서 감사합니다")
                    .toString());
            EmailCode emailCode1 = emailCodeRepository.findByUsername(emailRequestDto.getUsername()).orElse(null);
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
}
