package com.samsamohoh.webtoonsearch.application.port.in;

import lombok.Getter;

@Getter
public class RegisterUserCommand {
    private final String email;
    private final String gender;
    private final String naverId;
    private final String age;
    private final String nickname;

    public RegisterUserCommand(String email, String gender, String naverId, String age, String nickname) {
        this.email = email;
        this.gender = gender;
        this.naverId = naverId;
        this.age = age;
        this.nickname = nickname;
    }

}
