package com.samsamohoh.webtoonsearch.application.port.out;


import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;

public interface SaveMemberPort {
    void saveMember(RegisterMemberCommand registerMemberCommand);
}
