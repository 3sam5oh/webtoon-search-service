package com.samsamohoh.webtoonsearch.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ApiResponse<T> {
    boolean success;
    String message;
    T data;

    public ApiResponse(T data) {
        this.success = true;
        this.message = "Success";
        this.data = data;
    }
}
