package com.samsamohoh.webtoonsearch.adapter.api.member;

import java.util.Map;

public interface  OAuth2UserInfo {

    Map<String, Object> getAttributes();
    String getProviderId();
    String getEmail();
    String getAge();
    String getGender();
    String getNickname();
    String getProvider();
    String getName();

}
