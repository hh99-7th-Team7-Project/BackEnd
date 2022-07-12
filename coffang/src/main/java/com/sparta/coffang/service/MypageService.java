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


    //유저 프로필 변경
    public ResponseEntity updateUser(Long userId, MypageRequestDto requestDto, UserDetailsImpl userDetails) {

        //수정할 user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if(!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        String defaultImg = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/fbcebde7-ae14-42f0-9a75-261914c1053f.png"; //기본 이미지
        String nickname = requestDto.getNickname();
        String profileImage = requestDto.getProfileImage();

        //nickname 정규식 맞지 않는 경우 오류메시지 전달
        if(nickname.equals("")) {
            throw new CustomException(ErrorCode.EMPTY_NICKNAME);
        } else if (userRepository.findByNickname(nickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        } else if ( 2 > nickname.length() || 10 < nickname.length() ) {
            throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
        }

        if(nickname.equals(user.getUsername())) {
            nickname = user.getNickname();
        }

        if(profileImage.equals(user.getProfileImage())) {
            profileImage = user.getProfileImage();
        }
        // 기존 이미지가 있다면 S3서버에서 삭제 / 기본이미지는 삭제 없이 그냥 덮어쓰기
        if(!profileImage.equals(defaultImg)) {
            s3Service.deleteFile(user.getProfileImage());
        } else {
            profileImage = defaultImg;
        }

        // user 프로필 업데이트
        user.setNickname(nickname);
        user.setProfileImage(profileImage);
        //DB에 저장
        userRepository.save(user);

        //수정한 거 Dto에 저장해서 반환하기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);

        return ResponseEntity.ok().body(mypageResponseDto);
    }


    // 유저 정보 조회 (username, nickname, profileImage)
    public ResponseEntity getUserInfo(Long userId, UserDetailsImpl userDetails) {

        //user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if( user.getId() != userDetails.getUser().getId()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        //dto에 저장해서 내려다 주기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);

        return ResponseEntity.ok().body(mypageResponseDto);
    }
}