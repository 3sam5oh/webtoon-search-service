package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonDTO;

public interface SelectRecordUseCase {

    boolean addClickRecord(SelectWebtoonDTO selectWebtoonDTO);
}
