package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.RegisterMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<RegisterMemberEntity, Long> {
    RegisterMemberEntity findByProviderId(String providerId);
}
