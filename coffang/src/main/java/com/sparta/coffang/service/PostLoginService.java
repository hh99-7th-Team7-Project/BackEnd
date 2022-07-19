package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.dto.responseDto.PostPageResponseDto;
import com.sparta.coffang.dto.responseDto.PostResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.BookMark;
import com.sparta.coffang.model.Love;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.model.PostLove;
import com.sparta.coffang.repository.BookMarkRepository;
import com.sparta.coffang.repository.LoveRepository;
import com.sparta.coffang.repository.PostLoveRepository;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostLoginService {
    private final PostRepository postRepository;
    private final PostLoveRepository postLoveRepository;
    private final BookMarkRepository bookMarkRepository;
    public ResponseEntity getAllByCategory(String category,UserDetailsImpl userDetails) {
        List<Post> postList = postRepository.findAllByCategory(category);
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post,userDetails));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    //전체 받아오기
    public ResponseEntity getAll(UserDetailsImpl userDetails) {
        //timestamp 만들어지면 orderby로 find 바꾸기
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post,userDetails));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    //게시판 상세
    public ResponseEntity getDetail(Long id,UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        return ResponseEntity.ok().body(getDetailDto(post.get(),userDetails));
    }

    //검색
    public ResponseEntity search(String keyword,UserDetailsImpl userDetails){
        List<Post> postList = postRepository.findByTitleContainingIgnoreCase(keyword);
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

//        if(type.equals("title"))
//            postList = postRepository.findByTitleContainingIgnoreCase(keyword);
//        else
//            postList = postRepository.findByUserNicknameContainingIgnoreCase(keyword);

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post,userDetails));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    public PostResponseDto getDetailDto(Post post,UserDetailsImpl userDetails){
        PostLove postLove = postLoveRepository.findByUserIdAndPostId(userDetails.getUser().getId(), post.getId());
        BookMark bookMark = bookMarkRepository.findByUserIdAndPostId(userDetails.getUser().getId(), post.getId());
        boolean checkNew = false;
        int loveCount = 0;
        boolean loveCheck = false;
        boolean bookMarkCheck = false;


        if ((post.getLoveList() != null && post.getLoveList().size() > 0)) {
            loveCount = post.getLoveList().size();

            if ((postLoveRepository.existsByUserNicknameAndPostId((userDetails.getUser().getNickname()),post.getId())) &&
                    postLove.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
                loveCheck = true;
            } else {
                loveCheck = false;
            }
        }else{
        }

        //북마크
        if ((bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), post.getId())) &&
                bookMark.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
            bookMarkCheck = true;
        } else {
            bookMarkCheck = false;
        }

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .nickname(post.getUser().getNickname())
                .userImg(post.getUser().getProfileImage())
                .view(post.getView())
                .totalComment(post.getComments().size())
                .totalLove(loveCount)
                .loveCheck(loveCheck)
                .Bookmark(bookMarkCheck)
                .build();



        return postResponseDto;
    }

    public PostPageResponseDto getPageDto(Post post,UserDetailsImpl userDetails){
        PostLove postLove = postLoveRepository.findByUserIdAndPostId(userDetails.getUser().getId(), post.getId());
        BookMark bookMark = bookMarkRepository.findByUserIdAndPostId(userDetails.getUser().getId(), post.getId());
        boolean checkNew = false;
        int loveCount = 0;
        boolean loveCheck = false;
        boolean bookMarkCheck = false;


        if ((post.getLoveList() != null && post.getLoveList().size() > 0)) {
            loveCount = post.getLoveList().size();

            if ((postLoveRepository.existsByUserNicknameAndPostId((userDetails.getUser().getNickname()),post.getId())) &&
                    postLove.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
                loveCheck = true;
            } else {
                loveCheck = false;
            }
        }else{
        }

        //북마크
        if ((bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), post.getId())) &&
           bookMark.getUser().getNickname().equals(userDetails.getUser().getNickname())) {
            bookMarkCheck = true;
        } else {
            bookMarkCheck = false;
        }




        for (int i = 0; i <= 7; i++) {
            if (LocalDate.from(post.getCreatedAt().plusDays(i)).equals(LocalDate.now())) {
                checkNew = true;
                break;
            }
        }

        PostPageResponseDto postPageResponseDto = PostPageResponseDto.builder()
                .title(post.getTitle())
                .category(post.getCategory())
                .nickname(post.getUser().getNickname())
                .isNew(checkNew)
                .createdAt(post.getCreatedAt())
                .userImg(post.getUser().getProfileImage())
                .view(post.getView())
                .totalComment(post.getComments().size())
                .totalLove(loveCount)
                .loveCheck(loveCheck)
                .Bookmark(bookMarkCheck)
                .build();

        return postPageResponseDto;
    }


    @Transactional
    public void addView(Long id) {
        postRepository.updateView(id);
    }
}
