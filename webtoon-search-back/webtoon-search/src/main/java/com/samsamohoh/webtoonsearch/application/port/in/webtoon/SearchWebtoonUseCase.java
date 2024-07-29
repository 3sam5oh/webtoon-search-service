package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

public interface SearchWebtoonUseCase {
    WebtoonResult searchWebtoons(SearchWebtoonCommand command);
}
