package com.samsamohoh.webtoonsearch.adapter.oauth;

import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2UserAdapter implements OAuth2User {

    private final MemberProfileResponse memberProfile;
//    private String jwtToken; // For future JWT implementation

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", memberProfile.getId());
        attributes.put("name", memberProfile.getUsername());
        attributes.put("email", memberProfile.getEmail());
        attributes.put("provider", memberProfile.getProvider());
        attributes.put("provider_id", memberProfile.getProviderId()); // 제공자의 원래 식별자
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> memberProfile.getRole());
        return authorities;
    }

    @Override
    public String getName() {
        return memberProfile.getUsername();
    }

    // Convenience methods
    public Long getId() {
        return memberProfile.getId();
    }

    public String getEmail() {
        return memberProfile.getEmail();
    }

    public String getProvider() {
        return memberProfile.getProvider();
    }

    public String getProviderId() {
        return memberProfile.getProviderId();
    }

//    // For future JWT implementation
//    public void setJwtToken(String jwtToken) {
//        this.jwtToken = jwtToken;
//    }
//
//    public String getJwtToken() {
//        return jwtToken;
//    }
}
