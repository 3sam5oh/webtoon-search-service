package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonCommand;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.WebtoonResult;

public interface SearchWebtoonUseCase {
    WebtoonResult searchWebtoons(SearchWebtoonCommand command);
}
