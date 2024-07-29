package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.SelectWebtoonDTO;

public interface AddRecordPort {
    boolean addRecord(SelectWebtoonDTO dto);
}
