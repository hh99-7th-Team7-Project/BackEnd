package com.sparta.coffang.service;

import com.sparta.coffang.dto.PhotoDto;
import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.dto.responseDto.CoffeeResponseDto;
import com.sparta.coffang.dto.responseDto.PostPageResponseDto;
import com.sparta.coffang.dto.responseDto.PostResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

    public ResponseEntity savaPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .createdAt(LocalDateTime.now())
                .user(userDetails.getUser())
                .build();
        postRepository.save(post);
        return ResponseEntity.ok().body("작성 완료");
    }

    public ResponseEntity editPost(PostRequestDto postRequestDto, Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (!post.get().getUser().getNickname().equals(userDetails.getUser().getNickname()))
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        post.get().setPost(postRequestDto);
        postRepository.save(post.get());
        return ResponseEntity.ok().body(getDetailDto(post.get()));
    }

    public ResponseEntity delPost(Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (!post.get().getUser().getNickname().equals(userDetails.getUser().getNickname()))
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);

        postRepository.delete(post.get());
        return ResponseEntity.ok().body("삭제완료");
    }

    public ResponseEntity getAllByCategory(String category){
        List<Post> postList = postRepository.findAllByCategory(category);
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    //전체 받아오기
    public ResponseEntity getAll(){
        //timestamp 만들어지면 orderby로 find 바꾸기
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc();
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    //게시판 상세
    public ResponseEntity getDetail(Long id){
        Optional<Post> post = postRepository.findById(id);

        return ResponseEntity.ok().body(getDetailDto(post.get()));
    }

    //검색
    public ResponseEntity search(String keyword){
        List<Post> postList = postRepository.findByTitleContainingIgnoreCase(keyword);
        List<PostPageResponseDto> postPageResponseDtos = new ArrayList<>();

//        if(type.equals("title"))
//            postList = postRepository.findByTitleContainingIgnoreCase(keyword);
//        else
//            postList = postRepository.findByUserNicknameContainingIgnoreCase(keyword);

        for (Post post : postList) {
            postPageResponseDtos.add(getPageDto(post));
        }

        return ResponseEntity.ok().body(postPageResponseDtos);
    }

    public PostResponseDto getDetailDto(Post post){
        PostResponseDto postResponseDto = PostResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .nickname(post.getUser().getNickname())
                .userImg(post.getUser().getProfileImage())
                .view(post.getView())
                .build();

        return postResponseDto;
    }

    public PostPageResponseDto getPageDto(Post post){
        boolean checkNew = false;

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
                .build();

        return postPageResponseDto;
    }


    @Transactional
    public void addView(Long id) {
        postRepository.updateView(id);
    }
}
