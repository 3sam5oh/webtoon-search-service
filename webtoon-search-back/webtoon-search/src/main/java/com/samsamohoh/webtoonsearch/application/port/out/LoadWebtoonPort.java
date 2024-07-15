package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;

import java.util.List;

public interface LoadWebtoonPort {
    List<SearchableWebtoon> searchWebtoonsByTitle(String title);
}
