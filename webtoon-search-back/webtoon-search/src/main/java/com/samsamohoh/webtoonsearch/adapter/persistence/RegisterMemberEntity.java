package com.samsamohoh.webtoonsearch.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class RegisterMemberEntity {

    @Id
    @Column(name = "naver_id")
    private String naverId;

    private String email;
    private String gender;
    private String age;
    private String nickname;

    @Builder
    public RegisterMemberEntity(String naverId, String email, String gender, String age, String nickname) {
        this.naverId = naverId;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.nickname = nickname;
    }
}
