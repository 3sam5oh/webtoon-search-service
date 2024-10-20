package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberProfileResponse {
    private final long id;
    private final String providerId;
    private final String provider;
    private final String email;
    private final String username;
    private final String role;
    private final String age;
    private final String gender;
}
