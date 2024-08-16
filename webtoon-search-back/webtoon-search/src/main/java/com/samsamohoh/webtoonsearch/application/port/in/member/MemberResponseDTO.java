package com.samsamohoh.webtoonsearch.application.port.in.member;

import lombok.Value;

@Value
public class MemberResponseDTO {

    String providerId;
    String email;
    String age;
    String gender;
    String nickname;
    String provider;

}
