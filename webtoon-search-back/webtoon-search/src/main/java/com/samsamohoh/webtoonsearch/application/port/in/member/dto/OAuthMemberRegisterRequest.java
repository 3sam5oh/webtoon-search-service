package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuthMemberRegisterRequest {

    private String username;
    private String name;
    private String email;
    private String role;
    private String provider;
    private String providerId;
    private String age;
    private String gender;

}
