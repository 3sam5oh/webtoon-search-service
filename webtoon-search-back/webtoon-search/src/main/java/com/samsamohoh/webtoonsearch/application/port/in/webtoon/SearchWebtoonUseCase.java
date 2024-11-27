package com.samsamohoh.webtoonsearch.application.port.in.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonResponse;

import java.util.List;

public interface SearchWebtoonUseCase {
    List<SearchWebtoonResponse> searchWebtoons(SearchWebtoonRequest request);
}
