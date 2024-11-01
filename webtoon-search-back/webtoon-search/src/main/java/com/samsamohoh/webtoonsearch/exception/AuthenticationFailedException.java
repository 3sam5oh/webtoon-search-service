package com.samsamohoh.webtoonsearch.exception;

/**
 * 인증 실패 시 발생하는 예외
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
