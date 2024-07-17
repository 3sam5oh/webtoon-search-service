package com.samsamohoh.webtoonsearch.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.success = true;
        this.message = "Success";
        this.data = data;
    }
}
