package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthenticationResponse {
    boolean success;
    MemberProfileResponse memberProfile;
//    String token;  // JWT 토큰을 사용할 경우
    String status;
}
