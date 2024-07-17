package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.SearchableWebtoon;
import com.samsamohoh.webtoonsearch.application.port.in.SearchWebtoonUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.LoadWebtoonPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchWebtoonService implements SearchWebtoonUseCase {

    private final LoadWebtoonPort loadWebtoonPort;

    @Override
    @Transactional(readOnly = true)
    public List<SearchableWebtoon> searchWebtoons(String title) {
        if (title == null || title.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return loadWebtoonPort.searchWebtoonsByTitle(title.trim());
    }
}
