package com.samsamohoh.webtoonsearch.application.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberInfo {
    long id;
    String providerId;
    String provider;
    String email;
    String username;
    String role;
    String age;
    String gender;

    public boolean isAdult() {
        return Integer.parseInt(age) >= 18;
    }

    public boolean isValidEmail() {
        // 이메일 유효성 검사 로직
        return email != null && email.contains("@");
    }
}
