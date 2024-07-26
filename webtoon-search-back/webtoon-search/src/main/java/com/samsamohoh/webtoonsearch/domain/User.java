package com.samsamohoh.webtoonsearch.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    private String email;
    private String gender;
    private String naverId;
    private String age;
    private String nickname;

    public User(String email, String gender, String naverId, String age, String nickname) {
        this.email = email;
        this.gender = gender;
        this.naverId = naverId;
        this.age = age;
        this.nickname = nickname;
    }

}
