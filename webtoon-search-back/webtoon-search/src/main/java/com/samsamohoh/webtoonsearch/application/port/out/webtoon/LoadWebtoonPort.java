package com.samsamohoh.webtoonsearch.application.port.out.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.LoadWebtoonQuery;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;

public interface LoadWebtoonPort {
    WebtoonResult loadWebtoons(LoadWebtoonQuery query);
}
