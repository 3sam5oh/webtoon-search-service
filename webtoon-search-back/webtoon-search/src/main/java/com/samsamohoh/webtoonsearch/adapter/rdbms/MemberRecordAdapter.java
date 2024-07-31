package com.samsamohoh.webtoonsearch.adapter.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.MemberPersistenceAdapter;
import com.samsamohoh.webtoonsearch.adapter.persistence.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRecordAdapter implements SaveMemberPort {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    @Override
    public void saveMember(RegisterMemberCommand command) {
        RegisterMemberEntity entity = RegisterMemberEntity.builder()
                .naverId(command.getNaverId())
                .email(command.getEmail())
                .gender(command.getGender())
                .age(command.getAge())
                .nickname(command.getNickname())
                .build();
        memberPersistenceAdapter.save(entity);
    }
}
