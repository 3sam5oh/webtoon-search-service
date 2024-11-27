package com.samsamohoh.webtoonsearch.application.port.out.webtoon;

import com.samsamohoh.webtoonsearch.application.port.in.webtoon.dto.SearchWebtoonResponse;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonRequest;
import com.samsamohoh.webtoonsearch.application.port.out.webtoon.dto.LoadWebtoonResponse;

import java.util.List;

public interface LoadWebtoonPort {
    List<LoadWebtoonResponse> loadWebtoons(LoadWebtoonRequest query);
}
