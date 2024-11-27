package com.samsamohoh.webtoonsearch.application.port.out.member;

import com.samsamohoh.webtoonsearch.application.port.out.member.dto.AuthMemberResponse;

public interface AuthMemberPort {
    AuthMemberResponse saveMember(AuthMemberResponse authMemberResponse);
    AuthMemberResponse findByProviderAndProviderId(String provider, String providerId);
//    void deleteMember(String memberId);
}
