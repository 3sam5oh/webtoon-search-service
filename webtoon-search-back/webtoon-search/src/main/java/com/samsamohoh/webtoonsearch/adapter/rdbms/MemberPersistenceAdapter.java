package com.samsamohoh.webtoonsearch.adapter.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import com.samsamohoh.webtoonsearch.domain.MemberValidation;
import org.springframework.stereotype.Component;

@Component
public class MemberPersistenceAdapter implements SaveMemberPort {

    private final com.samsamohoh.webtoonsearch.adapter.persistence.MemberPersistenceAdapter memberPersistenceAdapter;

    public MemberPersistenceAdapter(com.samsamohoh.webtoonsearch.adapter.persistence.MemberPersistenceAdapter memberPersistenceAdapter) {
        this.memberPersistenceAdapter = memberPersistenceAdapter;
    }

    @Override
    public void saveUser(MemberValidation memberValidation) {
        RegisterMemberEntity registerMemberEntity = new RegisterMemberEntity();
        registerMemberEntity.setEmail(memberValidation.getEmail());
        registerMemberEntity.setGender(memberValidation.getGender());
        registerMemberEntity.setNaverId(memberValidation.getNaverId());
        registerMemberEntity.setAge(memberValidation.getAge());
        registerMemberEntity.setNickname(memberValidation.getNickname());
        memberPersistenceAdapter.save(registerMemberEntity);
    }
}
