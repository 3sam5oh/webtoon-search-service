package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.SelectRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectJpaRepository extends JpaRepository<SelectRecordEntity, String> {

}
