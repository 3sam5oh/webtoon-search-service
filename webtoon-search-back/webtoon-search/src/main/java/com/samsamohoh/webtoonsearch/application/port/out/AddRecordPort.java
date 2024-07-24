package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.adapter.web.SelectRecordRequest;
import com.samsamohoh.webtoonsearch.application.port.in.SelectWebtoonDTO;

public interface AddRecordPort {
    boolean addRecord(SelectWebtoonDTO dto);
}
