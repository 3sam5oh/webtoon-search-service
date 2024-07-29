package com.samsamohoh.webtoonsearch.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPersistenceAdapter extends JpaRepository<RegisterMemberEntity, String> {
}
