package com.example.fitlog_backend.global.exception;

import com.example.fitlog_backend.global.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
    return ResponseEntity
        .status(e.getStatus())
        .body(ApiResponse.fail(e.getMessage()));
  }
}