//package com.samsamohoh.webtoonsearch.adapter.api.member;
//
//import lombok.Getter;
//import lombok.ToString;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Map;
//
//@Getter
//@ToString
//public class PrincipalDetails implements UserDetails, OAuth2User {
//
//    private final String email;
//    private final String gender;
//    private final String providerId;
//    private final String age;
//    private final String nickname;
//    private final String provider;
//
//    private final OAuth2UserInfo oAuth2UserInfo;
//
//    public PrincipalDetails(String email, String gender, String providerId, String age, String nickname, String provider, OAuth2UserInfo oAuth2UserInfo) {
//        this.email = email;
//        this.gender = gender;
//        this.providerId = providerId;
//        this.age = age;
//        this.nickname = nickname;
//        this.provider = provider;
//        this.oAuth2UserInfo = oAuth2UserInfo;
//    }
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public String getPassword() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public Map<String, Object> getAttributes() {
//        return oAuth2UserInfo.getAttributes();
//    }
//
//    @Override
//    public String getName() {
//        return oAuth2UserInfo.getProviderId();
//    }
//
//    public String getProvider() {
//        return this.provider;
//    }
//}
