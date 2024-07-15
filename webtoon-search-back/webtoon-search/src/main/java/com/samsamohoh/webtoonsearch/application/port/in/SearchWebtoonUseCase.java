package com.samsamohoh.webtoonsearch.application.port.in;

import com.samsamohoh.webtoonsearch.application.dto.SearchWebtoonResultDTO;

import java.util.List;

public interface SearchWebtoonUseCase {
    List<SearchWebtoonResultDTO> searchWebtoons(String title);
}
