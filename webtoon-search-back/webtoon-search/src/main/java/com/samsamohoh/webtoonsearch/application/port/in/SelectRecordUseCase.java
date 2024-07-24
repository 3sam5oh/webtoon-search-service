package com.samsamohoh.webtoonsearch.application.port.in;

import com.samsamohoh.webtoonsearch.adapter.web.SelectRecordRequest;

public interface SelectRecordUseCase {

    boolean addClickRecord(SelectWebtoonDTO selectWebtoonDTO);
}
