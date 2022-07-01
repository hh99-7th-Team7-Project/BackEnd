package com.sparta.coffang.exceptionHandler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    /*
    403 FORBIDDEN : 권한이 없는 사용자
    */
    INVALID_AUTHORITY(HttpStatus.FORBIDDEN,"권한이 없는 사용자 입니다"),

    /*
    404 not found
     */
    API_NOT_FOUND(HttpStatus.NOT_FOUND, "잘못된 주소입니다.");


    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }
}
