package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.in.member.MemberResponseDTO;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRecordAdapter implements SaveMemberPort {

    private final MemberJpaRepository memberJpaRepository;

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

        memberJpaRepository.save(entity);

        return command; // 필요한 경우 savedEntity를 기반으로 새로운 DTO를 반환하도록 수정
    }

    @Override
    public MemberResponseDTO findMemberByProviderId(String providerId) {
        RegisterMemberEntity entity = memberJpaRepository.findByProviderId(providerId);
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
