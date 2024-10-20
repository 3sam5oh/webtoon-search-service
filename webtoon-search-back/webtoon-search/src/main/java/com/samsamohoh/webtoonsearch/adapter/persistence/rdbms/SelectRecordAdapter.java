package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.SelectRecordEntity;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonDTO;
import com.samsamohoh.webtoonsearch.application.port.out.AddRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class SelectRecordAdapter implements AddRecordPort {

    private final SelectJpaRepository selectJpaRepository;

    @Override
    public boolean addRecord(SelectWebtoonDTO data) {

        try {
            selectJpaRepository.save(toEntity(data));

            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private SelectRecordEntity toEntity(SelectWebtoonDTO dto) {

        return new SelectRecordEntity(
                dto.getId(),
                dto.getUrl(),
                dto.getTitle(),
                dto.getPlatform());
    }
}
