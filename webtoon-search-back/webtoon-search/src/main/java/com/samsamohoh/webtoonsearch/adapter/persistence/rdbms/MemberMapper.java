package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.out.dto.MemberPersistenceResponse;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public RegisterMemberEntity toEntity(MemberPersistenceResponse response) {
        return RegisterMemberEntity.builder()
                .id(response.getId())
                .providerId(response.getProviderId())
                .provider(response.getProvider())
                .email(response.getEmail())
                .username(response.getUsername())
                .role(response.getRole())
                .age(response.getAge())
                .gender(response.getGender())
                .build();
    }

    public MemberPersistenceResponse toResponse(RegisterMemberEntity entity) {
        return MemberPersistenceResponse.builder()
                .id(entity.getId())
                .providerId(entity.getProviderId())
                .provider(entity.getProvider())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .role(entity.getRole())
                .age(entity.getAge())
                .gender(entity.getGender())
                .build();
    }

}
