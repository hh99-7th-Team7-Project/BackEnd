package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.dto.responseDto.MypageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final S3Service s3Service;


    public ResponseEntity updateUserImage(Long userId, List<MultipartFile> profileImages, UserDetailsImpl userDetails) {

        //수정할 user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if( user.getId() != userDetails.getUser().getId()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String profileImage = "";
        //이미지가 null값이 들어올 때
        if(profileImages == null) {
            //기본이미지
            profileImage = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/47eb636c-2c94-461a-8261-1956cfbda8fc.png";
        } else {
            List<PhotoDto> photoDtos = s3Service.uploadFile(profileImages);
            profileImage = photoDtos.get(0).getPath();
        }

        // user 프로필 업데이트
        user.setProfileImage(profileImage);
        //DB에 저장
        userRepository.save(user);

        //수정한 거 Dto에 저장해서 반환하기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);

        return ResponseEntity.ok().body(mypageResponseDto);
    }


    public ResponseEntity updateUserName(Long userId, MypageRequestDto requestDto, UserDetailsImpl userDetails) {

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
        user.setUsername(username);
        user.setNickname(nickname);
        //DB에 저장
        userRepository.save(user);

        //수정한 거 Dto에 저장해서 반환하기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);

        return ResponseEntity.ok().body(mypageResponseDto);
    }
}
