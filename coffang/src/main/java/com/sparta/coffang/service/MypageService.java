package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.MypageRequestDto;
import com.sparta.coffang.dto.responseDto.MyCoffeeLoveResponseDto;
import com.sparta.coffang.dto.responseDto.MyBookMarkResponseDto;
import com.sparta.coffang.dto.responseDto.MypageResponseDto;
import com.sparta.coffang.dto.responseDto.PostPageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.*;
import com.sparta.coffang.repository.*;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LoveRepository loveRepository;
    private final BookMarkRepository bookMarkRepository;
    private final S3Service s3Service;
    private final PostService postService;

    //유저 프로필 변경
    public ResponseEntity updateUser(Long userId, MypageRequestDto requestDto, UserDetailsImpl userDetails) {
        //수정할 user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if (!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }


        String nicknamePattern = "^[a-zA-Z0-9ㄱ-ㅎ|ㅏ-ㅣ|가-힣~!@#$%^&*]{2,8}"; //닉네임 정규식 패턴
        String defaultImg = "https://coffang-jun.s3.ap-northeast-2.amazonaws.com/profileBasicImage.png"; //기본 이미지
        String nickname = requestDto.getNickname();
        String profileImage = requestDto.getProfileImage();

        //기존에 쓰던 같은 닉네임이 들어왔을 때
        if (nickname.equals(user.getNickname())) {
            nickname = user.getNickname();
        } else {
            //nickname 정규식 맞지 않는 경우 오류메시지 전달
            if (nickname.equals(""))
                throw new CustomException(ErrorCode.EMPTY_NICKNAME);
            else if (userRepository.findByNickname(nickname).isPresent())
                throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
            else if ( 2 > nickname.length() || 8 < nickname.length() )
                throw new CustomException(ErrorCode.NICKNAME_LEGNTH);
            else if (!Pattern.matches(nicknamePattern, nickname))
                throw new CustomException(ErrorCode.NICKNAME_WRONG);
        }

        //기존에 있던 이미지와 같은 이미지일 때
        if (profileImage.equals(user.getProfileImage())) {
            profileImage = user.getProfileImage();
        } else {
            // 기존 이미지가 있다면 S3서버에서 삭제 / 기본이미지는 삭제 없이 그냥 덮어쓰기
            if (!profileImage.equals(defaultImg))
                s3Service.deleteFile(user.getProfileImage());
            else
                profileImage = defaultImg;
        }

        // user 프로필 업데이트
        user.setNickname(nickname);
        user.setProfileImage(profileImage);
        userRepository.save(user); // DB에 저장

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
        if ( user.getId() != userDetails.getUser().getId()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        //dto에 저장해서 내려다 주기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);
        return ResponseEntity.ok().body(mypageResponseDto);
    }


    //내가 쓴 게시글 (posts)
    public ResponseEntity getMyBoard(Long userId, UserDetailsImpl userDetails) {
        //user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if (!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Post> myPostList = postRepository.findAllByUserId(userId);
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();
        for (Post myPost : myPostList) {
            postPageResponseDtos.add(postService.getPageDto(myPost));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    //내가 북마크한 커피
    public ResponseEntity getLikeCoffee(Long userId, UserDetailsImpl userDetails) {
        //user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if (!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Love> loveList = loveRepository.findAllByUserId(user.getId());
        List<MyCoffeeLoveResponseDto> myCoffeeLoveResponseDtos = new ArrayList<>();

        for (Love love : loveList) {
            MyCoffeeLoveResponseDto myCoffeeLoveResponseDto = MyCoffeeLoveResponseDto.builder()
                    .coffeeId(love.getCoffee().getId())
                    .nickname(user.getNickname())
                    .coffeeName(love.getCoffee().getName())
                    .img(love.getCoffee().getImg())
                    .brand(love.getCoffee().getBrand())
                    .category(love.getCoffee().getCategory())
                    .build();

            myCoffeeLoveResponseDtos.add(myCoffeeLoveResponseDto);
        }

        return ResponseEntity.ok().body(myCoffeeLoveResponseDtos);
    }

    //내가 북마크한 게시글
    public ResponseEntity getBookMarkPost(Long userId, UserDetailsImpl userDetails) {
        //user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if (!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<BookMark> bookMarkList = bookMarkRepository.findAllByUserId(user.getId());
        List<MyBookMarkResponseDto> myBookMarkResponseDtos = new ArrayList<>();

        for (BookMark bookMark : bookMarkList) {
            MyBookMarkResponseDto myBookMarkResponseDto = MyBookMarkResponseDto.builder()
                    .postId(bookMark.getPost().getId())
                    .nickname(user.getNickname())
                    .title(bookMark.getPost().getTitle())
                    .category(bookMark.getPost().getCategory())
                    .build();

            myBookMarkResponseDtos.add(myBookMarkResponseDto);
        }

        return ResponseEntity.ok().body(myBookMarkResponseDtos);
    }
}
