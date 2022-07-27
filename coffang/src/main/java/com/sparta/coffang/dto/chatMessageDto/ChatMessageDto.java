package com.sparta.coffang.dto.chatMessageDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
public class ChatMessageDto  {
    //    public class ChatMessageDto implements Serializable {
//    private static final long serialVersionUID = 6494678977089006639L;

    private String senderName;
    private String message;
    private String status;
    private String area;
    private Long chatpostId;
    private Long id;
    private int totalcount;
    private int count;
    private String profileImage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String opposingUserName;

    private long userCount;
    private String roomId;

}
