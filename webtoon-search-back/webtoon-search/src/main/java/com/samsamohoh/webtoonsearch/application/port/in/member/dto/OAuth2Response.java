package com.samsamohoh.webtoonsearch.application.port.in.member.dto;

public interface OAuth2Response {

    String getProviderID();
    String getProvider();
    String getEmail();
    String getName();

    // 선택 정보 (기본 구현 제공)
    default String getAgeRange() {
        return null;
    }

    default String getGender() {
        return null;
    }
}
