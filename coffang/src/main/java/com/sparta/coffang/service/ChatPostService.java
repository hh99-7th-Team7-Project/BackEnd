package com.sparta.coffang.service;


import com.sparta.coffang.dto.chatMessageDto.*;
import com.sparta.coffang.model.Attend;
import com.sparta.coffang.model.Calculator;
import com.sparta.coffang.model.ChatPost;
import com.sparta.coffang.model.User;
import com.sparta.coffang.repository.AttendRepository;
import com.sparta.coffang.repository.ChatPostRepository;
import com.sparta.coffang.repository.UserRepository;
import com.sparta.coffang.security.UserDetailsImpl;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
@Builder
@Slf4j
public class ChatPostService {
    private final ChatPostRepository chatPostRepository;
    private final UserRepository userRepository;
    private final AttendRepository attendRepository;
    private final Calculator calculator;

    // 채팅게시글 생성
    @Transactional
    public ChatPostResponseDto createChatpost(ChatPostRequestDto chatPostRequestDto, UserDetailsImpl userDetails) {
        if (chatPostRequestDto.getTotalcount() >= 3 && chatPostRequestDto.getTotalcount() <= 10) {
            System.out.println("왜 안나와요");
        } else {
        throw new NullPointerException("인원수는 3명에서 10명까지만 가능합니다");
    }

            User user = userRepository.findById(userDetails.getUser().getId()).orElse(null); //만든사람 ID값 추가시 필요한 메소드
            int count = 1;
            boolean completed = true;
            ChatPost chatPost = new ChatPost(chatPostRequestDto, count, completed, user);
            chatPostRepository.save(chatPost);
            Attend attend = new Attend(userDetails.getUser().getId(), chatPost.getChatpostId());
            attendRepository.save(attend);
            Long beforeTime = ChronoUnit.MINUTES.between(chatPost.getCreatedAt(), LocalDateTime.now());
            return new ChatPostResponseDto(chatPost, completed, calculator.time(beforeTime));
        }

    // 채팅게시글 수정
    public ChatPostDetailDto updateChatPost(Long chatpostId, User user, ChatPostRequestDto chatPostRequestDto) throws IOException {
        ChatPost chatPost = chatPostRepository.findById(chatpostId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다."));
        List<Attend> attend = attendRepository.findByChatpostId(chatpostId);
        List<ChatPostMemberDto> chatPostMemberDtos = new ArrayList<>();
        for (Attend attends : attend) {
            User users = userRepository.findById(attends.getUserId()).orElseThrow(
                    ()-> new IllegalArgumentException("user가 없습니다")
            );
            System.out.println(attends.getAttendId());//찍어보기
            chatPostMemberDtos.add(new ChatPostMemberDto(users));
        }
        if (!user.getId().equals(chatPost.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 수정 가능합니다.");
        }
        boolean completed;
        if (chatPostRequestDto.getTotalcount() <= chatPost.getCount()) {
            completed = false;
        } else {
            completed = true;
        }
        chatPost.update(chatPostRequestDto.getTitle(), chatPostRequestDto.getContents(), chatPostRequestDto.getCalendar(),
                chatPostRequestDto.getMap(),chatPostRequestDto.getMeettime(), chatPostRequestDto.getTotalcount(), completed);
        chatPostRepository.save(chatPost);
        Long beforeTime = ChronoUnit.MINUTES.between(chatPost.getCreatedAt(), LocalDateTime.now());
        return new ChatPostDetailDto(chatPost, chatPostMemberDtos, calculator.time(beforeTime));
    }

    // 게시글 삭제
    public void deleteChatPost(Long chatpostId, User user) {
        ChatPost chatPost = chatPostRepository.findById(chatpostId).orElseThrow(
                () -> new NullPointerException("해당 게시글이 존재하지 않습니다."));
        if (!user.getId().equals(chatPost.getUser().getId())) {
            throw new IllegalArgumentException("해당 게시글의 작성자만 삭제 가능합니다.");
        }
        try {
            chatPostRepository.deleteById(chatpostId);
        } catch (IllegalArgumentException e) {
            String detailMessage = String.format("채팅게시글 삭제 실패",chatPost.getTitle());
            log.info(detailMessage);
            throw new IllegalArgumentException(detailMessage);
        }
    }

    // 채팅게시글 디테일 조회
    @Transactional
    public ChatPostDetailDto findChatPost(Long chatpostId) {
        List<Attend> attend = attendRepository.findByChatpostId(chatpostId);
        List<ChatPostMemberDto> chatPostMemberDtos = new ArrayList<>();

        ChatPost chatPost = chatPostRepository.findById(chatpostId).orElse(null);
        boolean completed;
        for (Attend attends : attend) {
            User user = userRepository.findById(attends.getUserId()).orElseThrow(
                    ()-> new IllegalArgumentException("user가 없습니다")
            );
            System.out.println(attends.getAttendId());//찍어보기
            chatPostMemberDtos.add(new ChatPostMemberDto(user));
        }
        long beforeTime = ChronoUnit.MINUTES.between(chatPost.getCreatedAt(), LocalDateTime.now());
        if (chatPost.getTotalcount() <= chatPost.getCount()) {
            completed = false;
        } else {
            completed = true;
        }
        return new ChatPostDetailDto(chatPost, chatPostMemberDtos, completed, calculator.time(beforeTime));
    }
    // 채팅 게시글 참가 신청
    @Transactional
    public ChatPostAttendResponseDto attendChatPost(Long chatpostId, UserDetailsImpl userDetails) {
        Attend attend = attendRepository.findByChatpostIdAndUserId(chatpostId, userDetails.getUser().getId());
        ChatPost chatPost = chatPostRepository.findById(chatpostId).orElseThrow(
                () -> new IllegalArgumentException("참여할 채팅방이 없습니다."));
        // 중복 참여 검사
        String msg;
        if (attend == null) {
            Attend saveAttend = new Attend(userDetails.getUser().getId(), chatpostId);
            int result = chatPost.getCount() + 1;
            chatPost.updateCount(result);
            attendRepository.save(saveAttend);
            msg = "true";
        } else {
            attendRepository.deleteByChatpostIdAndUserId(chatpostId, userDetails.getUser().getId());
            int result = chatPost.getCount() - 1;
            chatPost.updateCount(result);
            msg = "false";
        }
        return  new ChatPostAttendResponseDto(msg);
    }

    // 채팅 게시글 전체 조회(페이지)
    public Page<ChatPostResponseDto> getAllChatpost(int pageNum) {
        List<ChatPost> chatPostList = chatPostRepository.findAllByOrderByCreatedAtDesc();
        Pageable pageable = getPageable(pageNum);
        List<ChatPostResponseDto>chatPostResponseDto = new ArrayList<>();
        setChatpostList(chatPostList, chatPostResponseDto);
        System.out.println("나옴1");
        int start = pageNum * 6;
        int end = Math.min((start + 6), chatPostList.size());
        System.out.println("나옴2");
        return overPagesChatpost(chatPostResponseDto, start, end, pageable, pageNum);
    }


    private void setChatpostList(List<ChatPost> chatpostList, List<ChatPostResponseDto> chatPostResponseDto) {
        for (ChatPost chatPost : chatpostList) {
            boolean completed = true;
            System.out.println("나옴3");
            if (chatPost.getTotalcount() <= chatPost.getCount()) {
                completed = false;
            }
            long beforeTime = ChronoUnit.MINUTES.between(chatPost.getCreatedAt(), LocalDateTime.now());
            ChatPostResponseDto chatPostResponseDtos = new ChatPostResponseDto(chatPost, completed, calculator.time(beforeTime));
            chatPostResponseDto.add(chatPostResponseDtos);
        }
    }

    public Page<ChatPostResponseDto> overPagesChatpost(List<ChatPostResponseDto> chatpostList, int start, int end, Pageable pageable, int page) {
        Page<ChatPostResponseDto> pages = new PageImpl<>(chatpostList.subList(start, end), pageable, chatpostList.size());
        if(page > pages.getTotalPages()) {
            throw new IllegalArgumentException("요청할 수 없는 페이지 입니다.");
        }
        return pages;
    }

    //페이지 정렬
    private Pageable getPageable(int page) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "id");
        return PageRequest.of(page, 6,sort);
    }
    // 채팅 게시글 전체 개수 조회
    public ResponseEntity getCpCount() {
        int cpCount = chatPostRepository.findAll().size();
        return ResponseEntity.ok().body(cpCount);
    }
}


