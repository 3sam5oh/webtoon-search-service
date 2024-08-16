package com.samsamohoh.webtoonsearch.application.service;


import com.samsamohoh.webtoonsearch.application.port.in.member.MemberResponseDTO;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService implements RegisterMemberUseCase {

    private final SaveMemberPort saveMemberPort;

    @Override
    public boolean updateMemberInfo(MemberResponseDTO memberResponseDTO) {
        MemberResponseDTO updatedMember = saveMemberPort.saveMember(memberResponseDTO);
        return updatedMember != null;
    }

    @Override
    public MemberResponseDTO getMemberInfo(String providerId) {
        return saveMemberPort.findMemberByProviderId(providerId);
    }
}
