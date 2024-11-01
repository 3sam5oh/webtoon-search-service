package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OAuthLoginRequest {
    String providerId;
    String provider;  // "naver", "google" 등
    String accessToken;  // OAuth 제공자로부터 받은 액세스 토큰
}
