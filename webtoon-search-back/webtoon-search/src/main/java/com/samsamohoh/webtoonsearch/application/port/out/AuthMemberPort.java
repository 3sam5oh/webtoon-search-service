package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.out.dto.AuthMemberResponse;

public interface AuthMemberPort {
    AuthMemberResponse saveMember(AuthMemberResponse authMemberResponse);
    AuthMemberResponse findByProviderAndProviderId(String provider, String providerId);
//    void deleteMember(String memberId);
}
