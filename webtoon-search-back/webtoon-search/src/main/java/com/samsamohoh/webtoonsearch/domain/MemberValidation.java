package com.samsamohoh.webtoonsearch.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberValidation {
    String email;
    String gender;
    String providerId;
    String age;
    String nickname;
    String provider;


}
