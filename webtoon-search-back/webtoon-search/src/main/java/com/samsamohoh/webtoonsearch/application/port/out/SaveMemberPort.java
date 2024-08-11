package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.in.member.MemberResponseDTO;

public interface SaveMemberPort {
    MemberResponseDTO saveMember(MemberResponseDTO memberResponseDTO);
    MemberResponseDTO findMemberByProviderId(String providerId);
}
