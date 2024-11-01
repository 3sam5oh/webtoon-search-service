package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.AuthMemberInfo;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import com.samsamohoh.webtoonsearch.application.port.out.dto.AuthMemberResponse;
import org.springframework.stereotype.Component;

/**
 * @- 회원 인증 관련 객체 변환을 담당하는 매퍼
 * @- 주요 책임:
 * @1. 영속성 계층과 도메인 모델 간의 데이터 변환
 * @2. 도메인 모델과 API 응답 DTO 간의 데이터 변환
 */
@Component
public class AuthMemberDomainMapper {

    /**
     * 영속성 응답 객체를 도메인 모델로 변환
     * @param dto 영속성 계층의 회원 정보
     * @return 도메인 모델로 변환된 회원 정보
     */
    public AuthMemberInfo toDomainModel(AuthMemberResponse dto) {
        if (dto == null) {
            throw new IllegalArgumentException("MemberPersistenceResponse cannot be null");
        }

        return AuthMemberInfo.createFromPersistence(
                dto.getId(),
                dto.getProviderId(),
                dto.getProvider(),
                dto.getEmail(),
                dto.getName(),
                dto.getRole(),
                dto.getAgeRange(),
                dto.getGender(),
                dto.getStatus()
        );
    }

    /**
     * 도메인 모델을 프로필 응답 DTO로 변환
     * @param authMemberInfo 도메인 모델의 회원 정보
     * @return 클라이언트에게 반환될 회원 프로필 정보
     */
    public MemberProfileResponse toProfileResponse(AuthMemberInfo authMemberInfo) {
        if (authMemberInfo == null) {
            throw new IllegalArgumentException("MemberInfo cannot be null");
        }

        return MemberProfileResponse.builder()
                .email(authMemberInfo.getEmail().getValue())
                .name(authMemberInfo.getName().getValue())
                .role(authMemberInfo.getRole().getValue())
                .ageRange(authMemberInfo.getAgeRange() != null ?
                        authMemberInfo.getAgeRange().getValue() : null)
                .gender(authMemberInfo.getGender() != null ?
                        authMemberInfo.getGender().getValue() : null)
                .build();
    }

    /**
     * 도메인 모델을 영속성 응답 DTO로 변환
     * @param authMemberInfo 도메인 모델의 회원 정보
     * @return 영속성 계층에 저장될 회원 정보
     */
    public AuthMemberResponse toPersistenceResponse(AuthMemberInfo authMemberInfo) {
        if (authMemberInfo == null) {
            throw new IllegalArgumentException("MemberInfo cannot be null");
        }

        return AuthMemberResponse.builder()
                .id(authMemberInfo.getId() != null ? authMemberInfo.getId().value() : 0)
                .providerId(authMemberInfo.getProviderId())
                .provider(authMemberInfo.getProvider().getValue())
                .email(authMemberInfo.getEmail().getValue())
                .name(authMemberInfo.getName().getValue())
                .role(authMemberInfo.getRole().getValue())
                .ageRange(authMemberInfo.getAgeRange() != null ?
                        authMemberInfo.getAgeRange().getValue() : null)
                .gender(authMemberInfo.getGender() != null ?
                        authMemberInfo.getGender().getValue() : null)
                .status(authMemberInfo.getStatus().name())
                .build();
    }
}
