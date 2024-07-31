package com.samsamohoh.webtoonsearch.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MemberValidation {

    private String email;
    private String gender;
    private String naverId;
    private String age;
    private String nickname;

}
