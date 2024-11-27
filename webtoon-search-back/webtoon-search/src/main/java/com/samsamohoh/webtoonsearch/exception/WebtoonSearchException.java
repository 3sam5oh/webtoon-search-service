package com.samsamohoh.webtoonsearch.exception;

/**
 * 웹툰 검색 관련 예외
 */
public class WebtoonSearchException extends RuntimeException {

    public WebtoonSearchException(String message) {
        super(message);
    }

    public WebtoonSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
