package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AuthenticationResponse {
    private boolean success;
    private MemberProfileResponse memberProfile;
    private String token;  // JWT 토큰을 사용할 경우

    public AuthenticationResponse(boolean success, MemberProfileResponse memberProfile) {
        this.success = success;
        this.memberProfile = memberProfile;
    }

    public AuthenticationResponse(boolean success, MemberProfileResponse memberProfile, String token) {
        this.success = success;
        this.memberProfile = memberProfile;
        this.token = token;
    }
}
