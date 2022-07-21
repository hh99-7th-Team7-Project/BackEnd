package com.sparta.coffang.dto.chatMessageDto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ChatPostListDto {
    private List<ChatPostResponseDto> chatpostList;
    private int totalPage;
    private int currentPage;

    public ChatPostListDto(Page<ChatPostResponseDto> chatPostResponseDtoPage) {
        this.chatpostList = chatPostResponseDtoPage.getContent();
        this.totalPage = chatPostResponseDtoPage.getTotalPages();
        this.currentPage = chatPostResponseDtoPage.getNumber();

    }
    }

