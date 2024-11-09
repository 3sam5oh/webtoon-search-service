package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SelectWebtoonRequest;

public interface AddRecordPort {
    boolean addRecord(SelectWebtoonRequest dto);
}
