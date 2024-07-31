package com.samsamohoh.webtoonsearch.application.port.in.member;

import lombok.Value;

@Value
public class RegisterMemberCommand {
    String email;
    String gender;
    String naverId;
    String age;
    String nickname;


}
