package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SelectRecordUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SelectWebtoonDTO;
import com.samsamohoh.webtoonsearch.application.port.out.AddRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SelectRecordService implements SelectRecordUseCase {

    private final AddRecordPort addRecordPort;

    @Override
    public boolean addClickRecord(SelectWebtoonDTO dto) {

        if (addRecordPort.addRecord(dto))
            return true;
        return false;
    }

}
