package com.samsamohoh.webtoonsearch.application.port.in;

public interface SearchWebtoonUseCase {
    WebtoonResult searchWebtoons(SearchWebtoonCommand command);
}
