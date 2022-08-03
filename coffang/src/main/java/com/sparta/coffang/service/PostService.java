package com.sparta.coffang.service;

import com.sparta.coffang.dto.requestDto.PostRequestDto;
import com.sparta.coffang.dto.responseDto.PostResponseDto;
import com.sparta.coffang.exceptionHandler.CustomException;
import com.sparta.coffang.exceptionHandler.ErrorCode;
import com.sparta.coffang.model.Post;
import com.sparta.coffang.report.ReportRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostLoveRepository postLoveRepository;
    private final BookMarkRepository bookMarkRepository;
    private final ReportRepository reportRepository;

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
        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDto(postList);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllWithLogIn(UserDetailsImpl userDetails, Pageable pageable) {
        Page<Post> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDtoWithLogIn(postList, userDetails);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllByCategory(String category, Pageable pageable) {
        Page<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDto(postList);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllByCategoryWithLogIn(String category, UserDetailsImpl userDetails, Pageable pageable) {
        Page<Post> postList = postRepository.findAllByCategoryOrderByCreatedAtDesc(category, pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDtoWithLogIn(postList, userDetails);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllOrderByLove(Pageable pageable) {
        Page<Post> postList = postRepository.findAllByOrderByLoveSizeDesc(pageable);

        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDto(postList);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity getAllOrderByLoveWithLogIn(UserDetailsImpl userDetails, Pageable pageable){
        Page<Post> postList = postRepository.findAllByOrderByLoveSizeDesc(pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDtoWithLogIn(postList, userDetails);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    //검색
    public ResponseEntity search(String keyword, Pageable pageable) {
        Page<Post> postList = postRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDto(postList);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity searchWithLogIn(String keyword, UserDetailsImpl userDetails, Pageable pageable) {
        Page<Post> postList = postRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword, pageable);
        HashMap<String, Object> response = new HashMap<>();

        List<PostResponseDto> postResponseDtos = getPageDtoWithLogIn(postList, userDetails);
        response.put("post", postResponseDtos);
        response.put("totalPage", postList.getTotalPages());
        return ResponseEntity.ok().body(response);
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
        postResponseDto.setIsReport(reportRepository.existsByUserIdAndReportIdAndCategory(userDetails.getUser().getId(), id, "게시글"));
        return ResponseEntity.ok().body(postResponseDto);
    }

    public List<PostResponseDto> getPageDto(Page<Post> postList) {
        List<PostResponseDto> postResponseDtos = postList.stream()
                .map(post -> new PostResponseDto(post))
                .collect(Collectors.toList());
        return postResponseDtos;
    }

    public List<PostResponseDto> getPageDtoWithLogIn(Page<Post> postList, UserDetailsImpl userDetails) {
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
