package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import com.samsamohoh.webtoonsearch.domain.MemberValidation;
import org.springframework.stereotype.Service;

@Service
public class RegisterMemberService implements RegisterMemberUseCase {

    private final SaveMemberPort saveMemberPort;

    public RegisterMemberService(SaveMemberPort saveMemberPort) {
        this.saveMemberPort = saveMemberPort;
    }

    @Override
    public void registerMember(RegisterMemberCommand command) {
        MemberValidation memberValidation = new MemberValidation(command.getEmail(), command.getGender(), command.getNaverId(), command.getAge(), command.getNickname());
        saveMemberPort.saveUser(memberValidation);
    }
}
