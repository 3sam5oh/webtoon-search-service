package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;

public interface SearchWebtoonUseCase {
    WebtoonResult searchWebtoons(SearchWebtoonRequest request);
}
