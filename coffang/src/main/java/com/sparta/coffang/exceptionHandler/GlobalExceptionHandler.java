package com.sparta.coffang.exceptionHandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<ErrorResponse> handleApiRequestException(CustomException ex) {
        return ErrorResponse.toResponseEntity(ex.getErrorCode());
    }
}
