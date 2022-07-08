package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.dto.requestDto.SignupRequestDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;


    public ResponseEntity updateUserProfile(Long userId, MypageRequestDto requestDto, PhotoDto photoDto, UserDetailsImpl userDetails) {

        //수정할 user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if( user.getId() != userDetails.getUser().getId()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"; //이메일 정규식 패턴
        String username = requestDto.getUsername();
        String nickname = requestDto.getNickname();
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


        // user 프로필 업데이트
        user.setNickname(username);
        user.setUsername(nickname);
        user.setProfileImage(profileImage);


        userRepository.save(user);

        return new ResponseEntity("프로필 수정이 완료되었습니다", HttpStatus.OK);
    }
}
