package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.in.WebtoonResult;

public interface LoadWebtoonPort {
    WebtoonResult loadWebtoons(LoadWebtoonQuery query);
}
