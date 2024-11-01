package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.AuthMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthMemberJpaRepository extends JpaRepository<AuthMemberEntity, Long> {
    // OAuth provider, providerId로 회원 조회
    AuthMemberEntity findByProviderAndProviderId(String provider, String providerId);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
