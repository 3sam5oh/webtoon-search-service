package com.samsamohoh.webtoonsearch.application.port.in;

import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;

import java.util.List;

public interface SearchWebtoonUseCase {
    List<SearchableWebtoon> searchWebtoons(String title);
}
