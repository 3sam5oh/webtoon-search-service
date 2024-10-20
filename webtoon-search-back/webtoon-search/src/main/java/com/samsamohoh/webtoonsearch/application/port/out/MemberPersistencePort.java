package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.out.dto.MemberPersistenceResponse;

public interface MemberPersistencePort {
    MemberPersistenceResponse saveMember(MemberPersistenceResponse memberPersistenceResponse);
    MemberPersistenceResponse findMemberByProviderId(String providerId);
    MemberPersistenceResponse findMemberById(String memberId);
    void deleteMember(String memberId);
}
