package com.samsamohoh.webtoonsearch.application.port.in.member;

import com.samsamohoh.webtoonsearch.application.port.in.member.dto.*;

/**
 * 인증 및 회원 관리를 위한 유스케이스 인터페이스,
 * 헥사고날 아키텍처의 포트 역할을 수행하며, 애플리케이션의 주요 비즈니스 기능을 정의
 */
public interface AuthenticationUseCase {

    /**
     * OAuth2.0 사용자 인증을 처리
     * 기존 회원의 경우 인증을 수행하고, 신규 사용자의 경우 회원 가입 후 인증을 진행
     *
     * @param oAuth2Response OAuth2.0 인증 제공자로부터 받은 사용자 정보
     * @return 인증 결과와 회원 프로필 정보를 포함한 응답
     */
    AuthenticationResponse authenticateOAuth2User(OAuth2Response oAuth2Response);

    /**
     * OAuth2.0 제공자 정보와 사용자 ID를 기반으로 회원 인증을 수행
     *
     * @param oAuthLoginRequest 로그인에 필요한 제공자 정보와 사용자 ID
     * @return 인증 결과와 회원 프로필 정보를 포함한 응답
     */
    AuthenticationResponse authenticateMember(OAuthLoginRequest oAuthLoginRequest);

    /**
     * OAuth2.0 인증 후 신규 회원을 등록
     * 중복 회원 검사를 수행하고, 회원 정보를 저장
     *
     * @param oAuthMemberRegisterRequest 회원 가입에 필요한 사용자 정보
     * @return 생성된 회원의 프로필 정보
     */
    MemberProfileResponse registerNewMember(OAuthMemberRegisterRequest oAuthMemberRegisterRequest);

    /**
     * 현재 인증된 사용자의 프로필 정보를 조회
     *
     * @param provider   OAuth2.0 제공자
     * @param providerId 제공자별 사용자 ID
     * @return 회원 프로필 정보
     */
    MemberProfileResponse getCurrentUserProfile(String provider, String providerId);

    /**
     * 회원 로그아웃 처리
     * 세션을 무효화하고 관련된 토큰을 폐기
     *
     * @param memberId 로그아웃할 회원의 ID
     * @throws IllegalArgumentException memberId가 유효하지 않은 경우
     */
    void logoutMember(String memberId);

//    /**
//     * 회원 탈퇴 처리
//     * 회원 정보를 삭제하고 관련된 리소스를 정리
//     *
//     * @param memberId 탈퇴할 회원의 ID
//     */
//    void withdrawMember(String memberId);
}
