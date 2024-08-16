package com.samsamohoh.webtoonsearch.adapter.persistence;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor
public class RegisterMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider_id")
    private String providerId;

    private String email;
    private String gender;
    private String age;
    private String nickname;

    private String provider;

    @Builder
    public RegisterMemberEntity(String providerId, String email, String gender, String age, String nickname, String provider) {
        this.providerId = providerId;
        this.email = email;
        this.gender = gender;
        this.age = age;
        this.nickname = nickname;
        this.provider = provider;
    }
}
