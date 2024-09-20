package com.samsamohoh.webtoonsearch.adapter.api.member;

import java.util.Map;

public class NaverUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;
    private final Map<String, Object> attributesResponse;

    public NaverUserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
        this.attributesResponse = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getProviderId() {
        return attributesResponse.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return attributesResponse.get("email").toString();
    }

    @Override
    public String getName() {
        return attributesResponse.get("name").toString();
    }

    @Override
    public String getAge() {
        return attributesResponse.get("age").toString();
    }

    @Override
    public String getGender() {
        return attributesResponse.get("gender").toString();
    }

    @Override
    public String getNickname() {
        return attributesResponse.get("nickname").toString();
    }
}
