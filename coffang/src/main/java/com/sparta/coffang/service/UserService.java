package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.AdminRequestDto;
import com.sparta.coffang.dto.requestDto.SignupImgRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.dto.responseDto.SocialLoginResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.User;
import com.sparta.coffang.model.UserRoleEnum;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${spring.admin.token}")
    String ADMIN_TOKEN;

    public ResponseEntity signupUser(SignupRequestDto requestDto, String image) {

        String passwordPattern = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}"; //영어, 숫자 8자이상 20이하
//영문, 숫자, 특수기호 4자이상 20이하 "(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{4,20}"
// "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}"; //영어, 숫자 8자이상 20이하
//        String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; //이메일 정규식 패턴
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"; //이메일 정규식 패턴
        String nicknamePattern = "^[a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣~!@#$%^&*]{2,8}"; //닉네임 정규식 패턴
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String profileImage = image;

        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals(""))
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        else if (!Pattern.matches(emailPattern, username))
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        else if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals(""))
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        else if (userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        else if ( 2 > nickname.length() || 8 < nickname.length() )
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        else if (!Pattern.matches(nicknamePattern, nickname))
            throw new CustomException(ErrorCode.NICKNAME_WRONG);

        //password 정규식 맞지 않는 경우 오류메시지 전달
        if(password.equals(""))
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        else if ( 8 > password.length() || 20 < password.length() )
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        else if (!Pattern.matches(passwordPattern, password))
            throw new CustomException(ErrorCode.PASSWORD_WRONG);

        password = passwordEncoder.encode(requestDto.getPassword()); // 패스워드 암호화
        User user = new User(username,nickname, password, profileImage);
        userRepository.save(user);
        return new ResponseEntity("회원가입을 축하합니다", HttpStatus.OK);
    }

    //username 중복체크
    public ResponseEntity checkUsername(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String emailPattern = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; //이메일 정규식 패턴

        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals(""))
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        else if (!Pattern.matches(emailPattern, username))
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        else if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        return new ResponseEntity("사용 가능한 이메일입니다.", HttpStatus.OK);
    }

    //nickname 중복체크
    public ResponseEntity checkNickname(SignupRequestDto requestDto) {
        String nickname = requestDto.getNickname();
        String nicknamePattern = "^[a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣~!@#$%^&*]{2,8}"; //닉네임 정규식 패턴

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals(""))
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        else if (userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        else if ( 2 > nickname.length() || 8 < nickname.length() )
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        else if (!Pattern.matches(nicknamePattern, nickname))
            throw new CustomException(ErrorCode.NICKNAME_WRONG);

        return new ResponseEntity("사용 가능한 닉네임입니다.", HttpStatus.OK);
    }

    //로그인 후 관리자 권한 얻을 수 있는 API
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

    //소셜로그인 사용자 정보 조회
    public ResponseEntity socialUserInfo(UserDetailsImpl userDetails) {
        //로그인 한 user 정보 검색
        User user = userRepository.findBySocialId(userDetails.getUser().getSocialId()).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //찾은 user엔티티를 dto로 변환해서 반환하기
        SocialLoginResponseDto socialLoginResponseDto = new SocialLoginResponseDto(user, true);
        return ResponseEntity.ok().body(socialLoginResponseDto);
    }

    //회원가입에 이미지가 null이 들어올 때
    public ResponseEntity signupNullUser(SignupImgRequestDto requestDto) {
        String passwordPattern = "(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}"; //영어, 숫자 8자이상 20이하
        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"; //이메일 정규식 패턴
        String nicknamePattern = "^[a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣~!@#$%^&*]{2,8}"; //닉네임 정규식 패턴
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
        String password = requestDto.getPassword();
        String profileImage = requestDto.getProfileImage();

        if(requestDto.getProfileImage() == null) {
            profileImage = "https://mytest-coffick.s3.ap-northeast-2.amazonaws.com/coffindBasicImage.png"; //기본이미지
        }

        //username 정규식 맞지 않는 경우 오류메시지 전달
        if(username.equals(""))
            throw new CustomException(ErrorCode.EMPTY_USERNAME);
        else if (!Pattern.matches(emailPattern, username))
            throw new CustomException(ErrorCode.USERNAME_WRONG);
        else if (userRepository.findByUsername(username).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals(""))
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        else if (userRepository.findByNickname(nickname).isPresent())
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        else if ( 2 > nickname.length() || 8 < nickname.length() )
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        else if (!Pattern.matches(nicknamePattern, nickname))
            throw new CustomException(ErrorCode.NICKNAME_WRONG);

        //password 정규식 맞지 않는 경우 오류메시지 전달
        if(password.equals(""))
            throw new CustomException(ErrorCode.EMPTY_PASSWORD);
        else if ( 8 > password.length() || 20 < password.length() )
            throw new CustomException(ErrorCode.PASSWORD_LEGNTH);
        else if (!Pattern.matches(passwordPattern, password))
            throw new CustomException(ErrorCode.PASSWORD_WRONG);

        password = passwordEncoder.encode(requestDto.getPassword()); // 패스워드 암호화
        User user = new User(username,nickname, password, profileImage);
        userRepository.save(user);
        return new ResponseEntity("회원가입을 축하합니다", HttpStatus.OK);
    }
}
