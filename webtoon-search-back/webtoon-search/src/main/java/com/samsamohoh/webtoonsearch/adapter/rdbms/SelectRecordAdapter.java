package com.samsamohoh.webtoonsearch.adapter.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.SelectPersistenceAdapter;
import com.samsamohoh.webtoonsearch.adapter.persistence.SelectRecordEntity;
import com.samsamohoh.webtoonsearch.application.port.in.SelectWebtoonDTO;
import com.samsamohoh.webtoonsearch.application.port.out.AddRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class SelectRecordAdapter implements AddRecordPort {

    private final SelectPersistenceAdapter selectPersistenceAdapter;

    @Override
    public boolean addRecord(SelectWebtoonDTO data) {

        try {
            selectPersistenceAdapter.save(toEntity(data));

            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private SelectRecordEntity toEntity(SelectWebtoonDTO dto) {

        return new SelectRecordEntity(
                0,
                dto.getId(),
                dto.getUrl(),
                dto.getTitle(),
                dto.getPlatform());
    }
}
