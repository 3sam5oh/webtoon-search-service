package com.samsamohoh.webtoonsearch.exception;

/**
 * 회원을 찾을 수 없을 때 발생하는 예외
 */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String provider, String providerId) {
        super(String.format("Member not found for provider: %s and providerId: %s", provider, providerId));
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
