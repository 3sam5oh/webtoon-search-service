package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberProfileResponse {
    String email;
    String name;
    String role;
    String ageRange;
    String gender;
}
