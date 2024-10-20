package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuthLoginRequest {
    private String username;
    private String provider;  // "naver", "google" 등
    private String accessToken;  // OAuth 제공자로부터 받은 액세스 토큰
}
