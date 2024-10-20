package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.RegisterMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.out.MemberPersistencePort;
import com.samsamohoh.webtoonsearch.application.port.out.dto.MemberPersistenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPersistencePort {

    private final MemberJpaRepository memberJpaRepository;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public MemberPersistenceResponse saveMember(MemberPersistenceResponse memberPersistenceResponse) {
        RegisterMemberEntity memberEntity = memberMapper.toEntity(memberPersistenceResponse);
        RegisterMemberEntity savedEntity = memberJpaRepository.save(memberEntity);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPersistenceResponse findMemberByProviderId(String providerId) {
        RegisterMemberEntity entity = memberJpaRepository.findByProviderId(providerId);
        return entity != null ? memberMapper.toResponse(entity) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberPersistenceResponse findMemberById(String memberId) {
        return memberJpaRepository.findById(Long.parseLong(memberId))
                .map(memberMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional
    public void deleteMember(String memberId) {
        memberJpaRepository.deleteById(Long.parseLong(memberId));
    }
}
