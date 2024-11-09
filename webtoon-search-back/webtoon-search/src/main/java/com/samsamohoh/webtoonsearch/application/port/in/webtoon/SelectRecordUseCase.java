package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonRequest;

public interface SelectRecordUseCase {

    boolean addClickRecord(SelectWebtoonRequest selectWebtoonRequest);
}
