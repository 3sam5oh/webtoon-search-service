package com.samsamohoh.webtoonsearch.adapter.oauth;

import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import com.samsamohoh.webtoonsearch.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

/**
 * Spring Security OAuth2User와 애플리케이션의 MemberProfile 간의 어댑터
 * Spring Security가 이해할 수 있는 형식으로 사용자 정보를 변환
 */
public class OAuth2UserAdapter implements OAuth2User {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserAdapter.class);
    private static final String UNKNOWN_USER = "Unknown User";

    // Spring Security에서 요구하는 필수 속성들
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_EMAIL = "email";

    private final MemberProfileResponse memberProfile;

    public OAuth2UserAdapter(MemberProfileResponse memberProfile) {
        validateMemberProfile(memberProfile);
        this.memberProfile = memberProfile;
        logger.debug("Created OAuth2UserAdapter for user: email={}",
                SecurityUtils.maskEmail(memberProfile.getEmail()));
    }

    @Override
    public Map<String, Object> getAttributes() {
        // 불변 맵 빌더 패턴 사용
        Map<String, Object> attributes = Map.of(
                ATTRIBUTE_NAME, memberProfile.getName(),
                ATTRIBUTE_EMAIL, memberProfile.getEmail()
        );

        validateRequiredAttributes(attributes);
        return attributes;

//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("provider", memberProfile.getProvider());
//        attributes.put("provider_id", memberProfile.getProviderId());
//        attributes.put("name", memberProfile.getName());
//        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = memberProfile.getRole();
        if (role == null) {
            logger.warn("User role is null, returning empty authorities");
            return Collections.emptyList();
        }

        try {
            // DB의 role 정보를 Spring Security의 권한 형식으로 변환
            return Collections.singleton(new SimpleGrantedAuthority(role));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role format: {}", role);
            return Collections.emptyList();
        }
    }

    // Convenience methods with null checks and logging
    @Override
    public String getName() {
        String name = memberProfile.getName();
        if (name == null) {
            logger.warn("Name is null, returning default value");
            return UNKNOWN_USER;
        }
        return name;
    }

    private void validateMemberProfile(MemberProfileResponse memberProfile) {
        if (memberProfile == null) {
            throw new IllegalArgumentException("MemberProfile cannot be null");
        }

        List<String> missingFields = new ArrayList<>();
        if (memberProfile.getName() == null) missingFields.add(ATTRIBUTE_NAME);
        if (memberProfile.getEmail() == null) missingFields.add(ATTRIBUTE_EMAIL);

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields missing in MemberProfile: " + String.join(", ", missingFields));
        }
    }

    private void validateRequiredAttributes(Map<String, Object> attributes) {
        Arrays.asList(ATTRIBUTE_NAME, ATTRIBUTE_EMAIL)
                .forEach(attr -> {
                    if (!attributes.containsKey(attr) || attributes.get(attr) == null) {
                        logger.error("Required attribute missing: {}", attr);
                        throw new IllegalStateException("Required attribute missing: " + attr);
                    }
                });
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


