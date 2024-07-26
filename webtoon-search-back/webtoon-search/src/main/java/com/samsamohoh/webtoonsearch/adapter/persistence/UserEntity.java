package com.samsamohoh.webtoonsearch.adapter.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")  // 테이블 이름 명시
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "naver_id")  // 컬럼 이름 명시
    private String naverId;

    private String email;

    private String gender;


    private String age;

    private String nickname;

}
