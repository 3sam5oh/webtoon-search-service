package com.samsamohoh.webtoonsearch.application.port.in.member;

public interface RegisterMemberUseCase {

    boolean updateMemberInfo(MemberResponseDTO memberResponseDTO);
    MemberResponseDTO getMemberInfo(String providerId);

}
