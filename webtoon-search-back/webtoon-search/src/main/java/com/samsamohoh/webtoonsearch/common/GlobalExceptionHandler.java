package com.samsamohoh.webtoonsearch.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, "처리 중 예상치 못한 오류가 발생했습니다.", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        ApiResponse<Void> response = new ApiResponse<>(false, "제목 '" + name + "'가 누락되었습니다.", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, "잘못된 인자가 전달되었습니다: " + ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointer(NullPointerException ex) {
        ApiResponse<Void> response = new ApiResponse<>(false, "null 참조 오류가 발생했습니다.", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
