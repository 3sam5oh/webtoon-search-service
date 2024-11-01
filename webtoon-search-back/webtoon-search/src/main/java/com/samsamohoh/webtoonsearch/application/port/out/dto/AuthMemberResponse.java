package com.samsamohoh.webtoonsearch.application.port.out.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthMemberResponse {

    long id;
    String providerId;
    String provider;
    String email;
    String name;
    String role;
    String ageRange;
    String gender;
    String status;
}
