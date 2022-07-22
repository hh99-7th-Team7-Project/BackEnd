package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.MypageRequestDto;

import com.sparta.coffang.dto.responseDto.MyCoffeeLoveResponseDto;
import com.sparta.coffang.dto.responseDto.MyBookMarkResponseDto;
import com.sparta.coffang.dto.responseDto.MypageResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.*;
import com.sparta.coffang.repository.*;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        User user = findUser(userId, userDetails);

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
        User user = findUser(userId, userDetails);

        //dto에 저장해서 내려다 주기
        MypageResponseDto mypageResponseDto = new MypageResponseDto(user);
        return ResponseEntity.ok().body(mypageResponseDto);
    }


    //내가 쓴 게시글 (posts)
    public ResponseEntity getMyBoard(Long userId, UserDetailsImpl userDetails) {
        findUser(userId, userDetails);

        List<Post> myPostList = postRepository.findAllByUserIdOrderByIdDesc(userId);
        return ResponseEntity.ok().body(postService.getPageDto(myPostList));
    }

    //내가 북마크한 커피
    public ResponseEntity getLikeCoffee(Long userId, UserDetailsImpl userDetails) {
        User user = findUser(userId, userDetails);

        List<Love> loveList = loveRepository.findAllByUserIdOrderByLoveIdDesc(user.getId());
        List<MyCoffeeLoveResponseDto> myCoffeeLoveResponseDtos = new ArrayList<>();

        for (Love love : loveList) {
            MyCoffeeLoveResponseDto myCoffeeLoveResponseDto = MyCoffeeLoveResponseDto.builder()

                    .id(love.getCoffee().getId())
                    .nickname(user.getNickname())
                    .name(love.getCoffee().getName())
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
        User user = findUser(userId, userDetails);

        List<BookMark> bookMarkList = bookMarkRepository.findAllByUserIdOrderByBookMarkIdDesc(userId);
        List<MyBookMarkResponseDto> myBookMarkResponseDtos = new ArrayList<>();

        for (BookMark bookMark : bookMarkList) {
            MyBookMarkResponseDto myBookMarkResponseDto = MyBookMarkResponseDto.builder()
                    .id(bookMark.getPost().getId())
                    .title(bookMark.getPost().getTitle())
                    .category(bookMark.getPost().getCategory())
                    .nickname(user.getNickname())
                    .createdAt(bookMark.getPost().getCreatedAt())
                    .userImg(bookMark.getUser().getProfileImage())
                    .view(bookMark.getPost().getView())
                    .totalComment(bookMark.getPost().getComments().size())
                    .totalLove(bookMark.getPost().getLoveList().size())
                    .build();

            myBookMarkResponseDtos.add(myBookMarkResponseDto);
        }

        return ResponseEntity.ok().body(myBookMarkResponseDtos);
    }

    //내가 쓴 글 갯수
    public ResponseEntity getMyBoardNum(Long userId, UserDetailsImpl userDetails) {
        findUser(userId, userDetails);

        int myBoardNum = postRepository.findAllByUserId(userId).size();
        return ResponseEntity.ok().body(myBoardNum);
    }

    //내가 참여한 모임수
    public ResponseEntity getMyChatNum(Long userId, UserDetailsImpl userDetails) {
        findUser(userId, userDetails);

        int myBoardNum = postRepository.findAllByUserId(userId).size();
        return ResponseEntity.ok().body(myBoardNum);
    }

    private User findUser(Long userId, UserDetailsImpl userDetails) {
        //user 정보 찾기
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
        );

        //userId 와 로그인한 사용자 Id 다를 때
        if (!user.getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return user;
    }
}
