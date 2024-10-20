package com.samsamohoh.webtoonsearch.application.port.out.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberPersistenceResponse {

    long id;
    String providerId;
    String provider;
    String email;
    String username;
    String role;
    String age;
    String gender;
}
