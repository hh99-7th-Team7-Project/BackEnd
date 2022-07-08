package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.AdminRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.User;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    //보안상 원래는 이렇게 '관리자 가입 토큰' 보여주면 안됨
    //이 토큰을 이메일 인증으로 돌리던지 해봐야겠다.
    private static final String ADMIN_TOKEN = "AAABnv/xRVklrnYxKZ0aHgTBcXukeZygoC";

    public ResponseEntity signupUser(SignupRequestDto requestDto, PhotoDto photoDto) {
        String passwordPattern = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}"; //영어, 숫자 8자이상 20이하
//영문, 숫자, 특수기호 4자이상 20이하 "(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{4,20}"
//        String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; //이메일 정규식 패턴
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"; //이메일 정규식 패턴
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String profileImage = photoDto.getPath();

        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        } else if (!Pattern.matches(emailPattern, username)) {
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        } else if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        } else if (userRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        } else if ( 2 > nickname.length() || 10 < nickname.length() ) {
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        }

        //password 정규식 맞지 않는 경우 오류메시지 전달
        if(password.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        } else if ( 8 > password.length() || 20 < password.length() ) {
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        } else if (!Pattern.matches(passwordPattern, password)) {
            throw new CustomException(ErrorCode.PASSWORD_WRONG);
        }

        //패스워드 암호화
        password = passwordEncoder.encode(requestDto.getPassword());

        //가입할 때 일반사용자로 로그인
        UserRoleEnum role = UserRoleEnum.USER;

        User user = new User(username,nickname, password, profileImage, role);
        userRepository.save(user);
        return new ResponseEntity("회원가입을 축하합니다", HttpStatus.OK);
    }

    public ResponseEntity checkUsername(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; //이메일 정규식 패턴

        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        } else if (!Pattern.matches(emailPattern, username)) {
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        } else if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        return new ResponseEntity("사용 가능한 이메일입니다.", HttpStatus.OK);
    }

    public ResponseEntity checkNickname(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        } else if (userRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        } else if ( 2 > nickname.length() || 10 < nickname.length() ) {
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        }

        return new ResponseEntity("사용 가능한 닉네임입니다.", HttpStatus.OK);
    }

    public ResponseEntity adminAuthorization(AdminRequestDto requestDto, UserDetailsImpl userDetails) {
        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!requestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new CustomException(ErrorCode.INVALID_AUTHORITY_WRONG); // 토큰값이 틀림
            }
            role = UserRoleEnum.ADMIN;
        }

        //역할 변경
        userDetails.getUser().setRole(role);
        //변경된 역할 저장
        userRepository.save(userDetails.getUser());

        return new ResponseEntity("관리자 권한으로 변경되었습니다", HttpStatus.OK);
    }
}
