package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.WebtoonResult;

public interface LoadWebtoonPort {
    WebtoonResult loadWebtoons(LoadWebtoonQuery query);
}
