package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.dto.responseDto.PostResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.repository.BookMarkRepository;
import com.sparta.coffang.repository.PostLoveRepository;
import com.sparta.coffang.repository.PostRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostLoveRepository postLoveRepository;
    private final BookMarkRepository bookMarkRepository;

    @Transactional
    public ResponseEntity savePost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .category(postRequestDto.getCategory())
                .createdAt(LocalDateTime.now())
                .user(userDetails.getUser())
                .loveSize(0L)
                .build();

        postRepository.save(post);
        return ResponseEntity.ok().body("작성 완료");
    }

    @Transactional
    public ResponseEntity editPost(PostRequestDto postRequestDto, Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (!post.get().getUser().getNickname().equals(userDetails.getUser().getNickname()))
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        post.get().setPost(postRequestDto);
        postRepository.save(post.get());

        PostResponseDto postResponseDto = new PostResponseDto(post.get());
        postResponseDto.setContent(post.get().getContent());
        postResponseDto.setLoveCheck(postLoveRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId()));
        postResponseDto.setBookmark(bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId()));
        return ResponseEntity.ok().body(postResponseDto);
    }

    @Transactional
    public ResponseEntity delPost(Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);

        if (!post.get().getUser().getNickname().equals(userDetails.getUser().getNickname()))
            throw new CustomException(ErrorCode.INVALID_AUTHORITY);
        postRepository.delete(post.get());
        return ResponseEntity.ok().body("삭제완료");
    }

    //전체 받아오기
    public ResponseEntity getAll(Pageable pageable) {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<PostResponseDto> postResponseDtos = postList.stream()
                .map(post -> new PostResponseDto(post))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(postResponseDtos);

//        return ResponseEntity.ok().body(getPageDto(postList));
    }

    public ResponseEntity getAllWithLogIn(UserDetailsImpl userDetails, Pageable pageable) {
        List<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<PostResponseDto> postResponseDtos = postList.stream()
                .map(post -> new PostResponseDto(post))
                .peek(postResponseDto -> postResponseDto.setLoveCheck(postLoveRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId())))
                .peek(postResponseDto -> postResponseDto.setBookmark(bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(postResponseDtos);
//        return ResponseEntity.ok().body(getPageDtoWithLogIn(postList, userDetails));
    }

    public ResponseEntity getAllByCategory(String category, Pageable pageable) {
        List<Post> postList = postRepository.findAllByCategory(category, pageable);
        return ResponseEntity.ok().body(getPageDto(postList));
    }

    public ResponseEntity getAllByCategoryWithLogIn(String category, UserDetailsImpl userDetails, Pageable pageable) {
        List<Post> postList = postRepository.findAllByCategory(category, pageable);
        return ResponseEntity.ok().body(getPageDtoWithLogIn(postList, userDetails));
    }

    public ResponseEntity getAllOrderByLove(Pageable pageable) {
        List<Post> postList = postRepository.findAllByOrderByLoveSizeDesc(pageable);
        System.out.println(postList.size());

        List<PostResponseDto> list = getPageDto(postList);
        System.out.println(list.size());
        return ResponseEntity.ok().body(getPageDto(postList));
    }

    public ResponseEntity getAllOrderByLoveWithLogIn(UserDetailsImpl userDetails, Pageable pageable){
        List<Post> postList = postRepository.findAllByOrderByLoveSizeDesc(pageable);
        return ResponseEntity.ok().body(getPageDtoWithLogIn(postList, userDetails));
    }

    //검색
    public ResponseEntity search(String keyword, Pageable pageable) {
        List<Post> postList = postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok().body(getPageDto(postList));
    }

    public ResponseEntity searchWithLogIn(String keyword, UserDetailsImpl userDetails, Pageable pageable) {
        List<Post> postList = postRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return ResponseEntity.ok().body(getPageDtoWithLogIn(postList, userDetails));
    }

    //게시판 상세
    public ResponseEntity getDetail(Long id) {
        Optional<Post> post = postRepository.findById(id);
        PostResponseDto postResponseDto = new PostResponseDto(post.get());
        postResponseDto.setContent(post.get().getContent());
        return ResponseEntity.ok().body(postResponseDto);
    }

    public ResponseEntity getDetailWithLogIn(Long id, UserDetailsImpl userDetails) {
        Optional<Post> post = postRepository.findById(id);
        PostResponseDto postResponseDto = new PostResponseDto(post.get());
        postResponseDto.setContent(post.get().getContent());
        postResponseDto.setLoveCheck(postLoveRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId()));
        postResponseDto.setBookmark(bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId()));
        return ResponseEntity.ok().body(postResponseDto);
    }

    public List<PostResponseDto> getPageDto(List<Post> postList) {
        List<PostResponseDto> postResponseDtos = postList.stream()
                .map(post -> new PostResponseDto(post))
                .collect(Collectors.toList());
        return postResponseDtos;
    }

    public List<PostResponseDto> getPageDtoWithLogIn(List<Post> postList, UserDetailsImpl userDetails) {
        List<PostResponseDto> postResponseDtos = postList.stream()
                .map(post -> new PostResponseDto(post))
                .peek(postResponseDto -> postResponseDto.setLoveCheck(postLoveRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId())))
                .peek(postResponseDto -> postResponseDto.setBookmark(bookMarkRepository.existsByUserNicknameAndPostId(userDetails.getUser().getNickname(), postResponseDto.getId())))
                .collect(Collectors.toList());
        return postResponseDtos;
    }

    @Transactional
    public void addView(Long id) {
        postRepository.updateView(id);
    }
}
