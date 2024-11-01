package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OAuthMemberRegisterRequest {
    String providerId;
    String provider;
    String email;
    String name;
    String role;
    String ageRange; // Optional ("10-19", "20-29")
    String gender;   // Optional ("M", "F")
    String status;
}
