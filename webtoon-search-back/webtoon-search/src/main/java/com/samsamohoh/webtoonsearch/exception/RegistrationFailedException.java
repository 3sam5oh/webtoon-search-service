package com.samsamohoh.webtoonsearch.exception;

/**
 * 회원 가입 처리 중 실패할 때 발생하는 예외
 */
public class RegistrationFailedException extends RuntimeException {

    public RegistrationFailedException(String message) {
        super(message);
    }

    public RegistrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
