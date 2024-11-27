package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.AuthMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.out.member.dto.AuthMemberResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMemberPersistenceMapper {
    public AuthMemberEntity toEntity(AuthMemberResponse response) {
        return AuthMemberEntity.builder()
                .id(response.getId())
                .providerId(response.getProviderId())
                .provider(response.getProvider())
                .email(response.getEmail())
                .name(response.getName())
                .role(response.getRole())
                .ageRange(response.getAgeRange())
                .gender(response.getGender())
                .status(response.getStatus())
                .build();
    }

    public AuthMemberResponse toResponse(AuthMemberEntity entity) {

        return AuthMemberResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProviderId())
                .provider(entity.getProvider())
                .email(entity.getEmail())
                .name(entity.getName())
                .role(entity.getRole())
                .ageRange(entity.getAgeRange())
                .gender(entity.getGender())
                .status(entity.getStatus())
                .build();
    }
}
