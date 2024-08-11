package com.samsamohoh.webtoonsearch.adapter.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.MemberPersistenceAdapter;
import com.samsamohoh.webtoonsearch.adapter.persistence.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.in.member.MemberResponseDTO;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRecordAdapter implements SaveMemberPort {

    private final MemberPersistenceAdapter memberPersistenceAdapter;

    @Override
    public MemberResponseDTO saveMember(MemberResponseDTO command) {
        RegisterMemberEntity entity = RegisterMemberEntity.builder()
                .providerId(command.getProviderId())
                .email(command.getEmail())
                .gender(command.getGender())
                .age(command.getAge())
                .nickname(command.getNickname())
                .provider(command.getProvider())
                .build();

        memberPersistenceAdapter.save(entity);

        return command; // 필요한 경우 savedEntity를 기반으로 새로운 DTO를 반환하도록 수정
    }

    @Override
    public MemberResponseDTO findMemberByProviderId(String providerId) {
        RegisterMemberEntity entity = memberPersistenceAdapter.findByProviderId(providerId);
        if (entity != null) {
            return new MemberResponseDTO(
                    entity.getProviderId(),
                    entity.getEmail(),
                    entity.getAge(),
                    entity.getGender(),
                    entity.getNickname(),
                    entity.getProvider()
            );
        }
        return null;
    }
}
