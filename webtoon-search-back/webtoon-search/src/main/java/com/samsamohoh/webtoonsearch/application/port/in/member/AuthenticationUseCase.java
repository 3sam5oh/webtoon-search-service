package com.samsamohoh.webtoonsearch.application.port.in.member;

import com.samsamohoh.webtoonsearch.application.exception.UserAlreadyExistsException;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.AuthenticationResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuthLoginRequest;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuthMemberRegisterRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationUseCase {

    // OAuth2.0 로그인
    AuthenticationResponse authenticateMember(OAuthLoginRequest oAuthLoginRequest) throws AuthenticationException;

    // OAuth2.0 인증 후 회원 정보가 없는 경우 새로 생성
    MemberProfileResponse registerNewMember(OAuthMemberRegisterRequest oAuthMemberRegisterRequest) throws UserAlreadyExistsException;

    // OAuth2.0 로그아웃
    void logoutMember(String memberId) throws UsernameNotFoundException;

    // 현재 사용자 프로필 정보 조회
    MemberProfileResponse getCurrentUserProfile(String memberId) throws UsernameNotFoundException;

    // OAuth2.0 회원 탈퇴
    void withdrawMember(String memberId) throws UsernameNotFoundException;

}
